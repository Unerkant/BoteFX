package BoteFx.controller;

import BoteFx.controller.fragments.FreundeCellController;
import BoteFx.model.Usern;
import BoteFx.service.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Controller
public class EinladenController implements Initializable {

    public ImageView einladenBildClose;
    @Autowired
    private TranslateService translate;
    @Autowired
    private MethodenService methodenService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ChatBoxController chatBoxController;
    @Autowired
    private FreundeController freundeController;
    @Autowired
    private FreundeCellController freundeCellController;
    @Autowired
    private ApiService apiService;
    @Autowired
    private LayoutService layoutService;


    @FXML private AnchorPane einladenAnchorPane;
    @FXML private ScrollPane einladenScrollPane;
    @FXML private VBox einladenVBox;
    @FXML private TextField einladenTextField;
    @FXML private Label einladenFehler;
    @FXML private Hyperlink einladungOk;
    @FXML private Hyperlink einladungSenden;

    @FXML private Label alleUserCount;
    @FXML private TableView<Usern> einladenTable;
    @FXML private TableColumn<Usern, Long> einladenCount;
    @FXML private TableColumn<Usern, String> einladenMail;
    @FXML private TableColumn<Usern, String> einladenName;


    /**
     * Eiladen Box auf 100% width ziehen
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(einladenAnchorPane, 0.0);
        AnchorPane.setRightAnchor(einladenAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(einladenAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(einladenAnchorPane, 0.0);
        einladenVBox.prefWidthProperty().bind(einladenScrollPane.widthProperty());

    }


    /**
     * Mein Token, zugesendet von FreundeController zeile: 330
     */
    private String meinerToken;
    public String getMeinerToken() {
        return meinerToken;
    }
    public void setMeinerToken(String meinerToken) {
        this.meinerToken = meinerToken;
    }



    /**
     * String immer kleinschreiben
     */
    public void inhaltLowerCase() {
        einladenTextField.textProperty().addListener((akVal, oldVal, neuVal) ->{
            einladenTextField.setText(neuVal.toLowerCase().replace(" ", ""));
        });
    }


    /**
     * Max. Lange von 254 Zeichen
     */
    public void inhaltLange() {
        int mailMax    = configService.MAIL_LENGTH;
        int telefonMax = configService.MAX_TEL_LENGTH;
        int eingabeLange   = einladenTextField.getText().length();
        String eingabeText = einladenTextField.getText();

        if (eingabeText.matches("[0-9]*")){
            if (eingabeLange > telefonMax) {
                einladenTextField.setText(eingabeText.substring(0, telefonMax));
                einladenTextField.positionCaret(eingabeText.length());
                einladenFehlerAusgabe("telefonzulang", "no");
            } else {
                einladenFehlerAusgabe("fehlerAusblenden", "ok");
            }

        } else {
            if (eingabeLange > mailMax){
                einladenTextField.setText(eingabeText.substring(0, mailMax));
                einladenTextField.positionCaret(eingabeText.length());
                einladenFehlerAusgabe("mailzulang", "no");
            } else {
                einladenFehlerAusgabe("fehlerAusblenden", "ok");
            }
        }

    }


    /**
     * der OK Button
     *
     * 1. leerzeichen ausschneiden
     * 2. prüfen auf Zahl (mail oder Telefon)
     * 3.
     *
     */
    public void inhaltValidate() {
        int telefonMin  = configService.MIN_TEL_LENGTH;
        int eingabeLang = einladenTextField.getText().length();
        String fieldValue = einladenTextField.getText();
        // Leeres Feld, Return
        if (fieldValue.isBlank()){
            einladenFehlerAusgabe("leeresfield", "no");
            // TextFiel in Focus setzen
            Platform.runLater( () -> einladenTextField.requestFocus());
            return;
        }

        if (fieldValue.matches("[0-9]*")){
            if (eingabeLang < 6){
                einladenFehlerAusgabe("telefonzukurz", "no");
                return;
            }
            // Request starten
            einladenRequest(fieldValue, "telefon");
        } else {
            // Mail Prüfen
            boolean valid = methodenService.mailValid(fieldValue);
            if (!valid){
                einladenFehlerAusgabe("falschemail", "no");
                return;
            }
            // Request starten
            einladenRequest(fieldValue, "mail");
        }
    }


    /**
     * GESTEUERT VON inhaltValidate(){....}
     *
     * gesendet wird an Bote/ApiUserController Zeile: ab 60
     * gesendete Daten:
     * 1. E-Mail-adresse oder telefonnummer (was wird in Textfeld eingegeben)
     * 2. key: 'mail' oder 'telefon', erkennungswort für zugesendeten mail oder Telefon
     *
     * Response von Bote/ApiUserController
     * 1. 'plusFreund': bei fund einer E-Mail oder Telefon
     * 2. 'noMail': keine E-Mail in Datenbank vorhanden
     * 3. 'noTelefon': kein Telefon in Datenbank vorhanden
     * 4. 'noFund': wenn keinen/falschen key abgesendet wird
     *
     * @param telMail
     * @param key
     */
    private void einladenRequest(String telMail, String key){

        // Request & response an/von Bote
        String reqUrl = configService.FILE_HTTP+"einladenApi";
        String jsonData = "{ \"sendeTelMail\":\""+ telMail +"\", \"sendeKey\":\""+key+"\", \"sendeToken\":\""+meinerToken+"\" }";
        HttpResponse<String> response = apiService.requestAPI(reqUrl, jsonData);
        String output = response.body();

        switch (output){
            case "plusFreund":          einladenFehlerAusgabe("freundplus", "ok");
                                        freundeController.freundeLaden(meinerToken);
                                        einladenClose();
                                        break;
            case "eigenesMail":         einladenFehlerAusgabe("eigenesmail", "no");
                                        break;
            case "noMail":              einladenFehlerAusgabe("nomail", "no");
                                        okButtonNoAktiv();
                                        break;
            case "eigenesTelefon":      einladenFehlerAusgabe("eigenestelefon", "no");
                                        break;
            case "noTelefon":           einladenFehlerAusgabe("notelefon", "no");
                                        okButtonNoAktiv();
                                        break;
            case "noFound":             einladenFehlerAusgabe("nofound", "no");
                                        break;
            case "schonFreund":         einladenFehlerAusgabe("schonfreund", "no");
                                        break;
            default:                    einladenFehlerAusgabe("allerror", "no"); break;
        }


    }

    /* *************** Bekannten per Mail oder Telefon einladen ************************ */

    /**
     * Click-Button = 'Bekannten zu Bote Einladen'
     *
     * wenn in Datenbank keine E-Mail Oder Telefonnummer gefunden wird
     * dann anderer Button(Bekannten zu Bote Einladen) aktivieren, eine Mail oder SMS wird an die
     * eingegebene-Adresse gesendet...
     *
     * response Status OK: mail = 200, telefon = 200
     * response nicht OK:  mail = 302, telefon = 500
     */
    public void einladungPerMail() {
        String einladungsMail = einladenTextField.getText();
        fehlerAusblenden(0);

        if (einladungsMail.matches("[0-9]*")){

            // Request & response an/von Bote
            String urlApi = configService.FILE_HTTP+"perSmsEinladen";
            String json = "{ \"sendTelefon\":\""+ einladungsMail +"\" }";
            HttpResponse<String> response = apiService.requestAPI(urlApi, json);
            int freiCode = response.statusCode();
            if (freiCode == 200){

                einladenFehlerAusgabe("smsgesendet", "ok");

            } else {

                einladenFehlerAusgabe("smsnichtgesendet", "no");

            }

        } else {

            // Request & response an/von Bote
            String urlApi = configService.FILE_HTTP+"perMailEinladen";
            String json = "{ \"sendMail\":\""+ einladungsMail +"\" }";
            HttpResponse<String> response = apiService.requestAPI(urlApi, json);
            int statusCode = Integer.parseInt(response.body());

            if (statusCode == 200){

                einladenFehlerAusgabe("mailgesendet", "ok");

            } else {

                einladenFehlerAusgabe("mailnichtgesendet", "no");

            }

        }

        // Button + TextFiel auf werkeinstellung setzen
        okButtonAktiv();
        einladenTextField.clear();
        einladenTextField.requestFocus();
    }


    /**
     * Ok Button zurücksetzen
     *
     * wenn keine mail oder Telefonnummer in Datenbank gefunden wird
     * (output: noMail/noTelefon, unten in switch) dann wird den Button mit den schrift
     * 'Bekannten zu Bote Einladen' aktiviert und den 'OK' button deaktiviert...
     *
     * wenn versuchen sie die mail oder Telefonnummer zu korrigiert dann wird alle buttons
     * zurückgesetzt, 'OK'= aktiviert + 'Bekannten zu Bote Einladen' = deaktiviert
     * @param keyEvent
     */
    public void okButtonZurucksetzen(KeyEvent keyEvent) {

        if (einladungOk.isVisible() == false){
            okButtonAktiv();
        }
        //System.out.println("ELSE");
    }


    /**
     * 2 untere Methoden, Button-rücksetzer-style
     *
     * Buttons 'OK' + 'Bekannten zu Bote Einladen' zurücksetzen
     *
     */
    private void okButtonAktiv(){
        einladungOk.setText("OK");
        einladungOk.setVisible(true);
        einladungSenden.setVisible(false);
        einladungSenden.setText("");
    }
    private void okButtonNoAktiv(){
        einladungOk.setText("");
        einladungOk.setVisible(false);
        einladungSenden.setVisible(true);
        einladungSenden.setText("Bekanten zu Bote Einladen");
    }



    /* *** Alle User-Mail/Telefonnummer anzeigen + zurück-Button + abbrechen-Button + Fehler-Anzeige ***  */


    /**
     * SPÄTER LÖSCHEN ODER ÄNDERN
     *
     * Anzeige von allen Usern (click auf dem männchen mit plus)
     *
     * SETTER & GETTER
     * alle User-Daten von FreundeController zugesendet Zeile: 330
     *
     * TableView ausgabe:
     * 0. zugesendete User-Daten sind in json-ListArray 'userns' gespeichert
     * 1. für die TableView Anzeige brauchen nur 2 parameter, pseudonym & e-mail
     * 2. deklarieren die TableColumn (einladenCount.setCellValueFactory(new Proper....)
     * 3. in eine observableArrayList werden 'pseudonym & E-Mail' gespeichert(for schleife)
     * 4. die observableArrayList wird in table geladen (angezeigt werden nur deklarierte TableColumn. pos.2)
     * 5. mit dem Doppelt click auf die E-Mail wird sie in obersten TextView geschrieben
     *
     */
    private String users;
    public void setUserdaten(String user){
        this.users = user;
        userAnzeigen(users);
    }
    ObservableList<Usern> list;
    private void userAnzeigen(String allUser){

        // response als array-list erstellen
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Usern>>(){}.getType();
        ArrayList<Usern> userns = gson.fromJson(allUser, listType);

        // TableColumn deklarieren
        einladenCount.setCellValueFactory(new PropertyValueFactory<Usern, Long>("id"));
        einladenName.setCellValueFactory(new PropertyValueFactory<Usern, String>("pseudonym"));
        einladenMail.setCellValueFactory(new PropertyValueFactory<Usern, String>("email"));

        // List-Array erstellen
        list = FXCollections.observableArrayList();
        int count = 1;

        // list-array mit daten fühlen für den TableColumn
        for (Usern user : userns){
            list.addAll(new Usern(count, "leer", "leer", "leer", user.getPseudonym(),
            "leer","leer", user.getTelefon().isBlank() ? user.getEmail() : user.getTelefon(), "leer", "leer", "leer"));
            count++;
        }

        // List-Array in Table ausgeben
        einladenTable.setItems(list);

        // Alle registrierte Nutzer zählen
        int totalCount = count - 1;
        alleUserCount.setText("Anzahl der Nutzer: " + totalCount);

        // TableView Autoresize + scroll ausblenden
        einladenTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        einladenCount.prefWidthProperty().bind(einladenTable.widthProperty().divide(4)); // w * 1/4
        einladenName.prefWidthProperty().bind(einladenTable.widthProperty().divide(4)); // w * 1/4
        einladenMail.prefWidthProperty().bind(einladenTable.widthProperty().divide(2)); // w * 1/2

        // geklicktes mail ins TextField kopieren
        einladenTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2){
                Usern usernList;
                usernList = einladenTable.getSelectionModel().getSelectedItem();
                einladenTextField.setText(usernList.getEmail());
                // Fehler anzeige Ausblenden
                einladenFehler.setText("");
            }
        });

        // TextFiel in Focus setzen
        Platform.runLater( () -> einladenTextField.requestFocus());

    }


    /**
     * der blaue Kreuzbutton + Abbrechen-Button
     */
    @FXML
    public void einladenClose() {
        // in Element alles Löschen
        //einladenAnchorPane.getChildren().clear();
        // selber Element Löschen
        //((Pane) einladenAnchorPane.getParent()).getChildren().remove(einladenAnchorPane);
        // nur, wenn App unter width 650px verkleinert wird
        translate.closeStackPane();
    }


    /**
     * Allgemeine Fehler ausgeben
     * @param fehler
     * @param ok
     */
    public void einladenFehlerAusgabe(String fehler, String ok){

        einladenFehler.setTextFill(ok == "ok" ? Color.GREEN : Color.RED);

        switch (fehler){

            case "leeresfield":         einladenFehler.setText("Text Feld darf nicht leer sein");
                                        fehlerAusblenden(5);
                                        break;
            case "telefonzukurz":       einladenFehler.setText("Telefonnummer zu Kurz");
                                        break;
            case "telefonzulang":       einladenFehler.setText("Telefonnummer zu Lang");
                                        break;
            case "falschemail":         einladenFehler.setText("Falsche E-Mail-Adresse");
                                        break;
            case "eigenestelefon":      einladenFehler.setText("Eigenes Telefonnummer nicht erlaubt");
                                        break;
            case "notelefon":           einladenFehler.setText("Mit dieser Telefonnummer ist noch niemand bei Bote registriert");
                                        break;
            case "smsgesendet":         einladenFehler.setText("eine Einladung zu Bote werde an " +
                                        "diese Telefonnummer ["+ einladenTextField.getText() +"] per SMS gesendet ");
                                        break;
            case "smsnichtgesendet":    einladenFehler.setText("SMS Versand an diesem Telefonnummer [\"+ einladenTextField.getText() +\"]" +
                                            " nicht möglich!. ApiEinladenController Zeile: 260 ");
                                        break;
            case "mailzulang":          einladenFehler.setText("erlaubt sind maximale Länge von 254 Zeichen.");
                                        break;
            case "nomail":              einladenFehler.setText("Mit dieser E-Mail ist noch niemand bei Bote registriert");
                                        break;
            case "eigenesmail":         einladenFehler.setText("eigenes E-Mal, nicht erlaubt");
                                        break;
            case "mailgesendet":        einladenFehler.setText("eine Einladung zu Bote werde an " +
                                        "diese E-Mail-Adresse ["+ einladenTextField.getText() +"] gesendet");
                                        break;
            case "mailnichtgesendet":   einladenFehler.setText("Einladung wurde nicht verschckt... ApiEinladenController Zeile: 250 ");
                                        break;
            case "fehlerAusblenden":    einladenFehler.setText(null);
                                        break;
            case "nofund":              einladenFehler.setText("ERROR: falsches key(Bote/ApiUserController/125)");
                                        break;
            case "allerror":            einladenFehler.setText("Allgemein Fehler: EiladenController Zeile: 190");
                                        break;
            case "freundplus":          einladenFehler.setText("Bekannten ist eingeladen");
                                        break;
            case "schonfreund":         einladenFehler.setText("als Freund schon gelistet");
                                        break;
            default:                    einladenFehler.setText("Allgemein Fehler: EinladenController Zeile: 350"); break;
        }

    }


    /**
     * Fehler Feld automatisch nach 5 sekunden ausblenden
     * benutzt wird nur von einladenFehlerAusgabe (hier gleich oben)
     */
    private void fehlerAusblenden(int sek){
        PauseTransition pause = new PauseTransition(Duration.seconds(sek));
        pause.setOnFinished(e -> einladenFehler.setText(null));
        pause.play();
    }

}
