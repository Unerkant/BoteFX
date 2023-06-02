package BoteFx.controller.fragments;

import BoteFx.Enums.GlobalView;
import BoteFx.controller.ChatBoxController;
import BoteFx.controller.FreundeController;
import BoteFx.controller.MessageController;
import BoteFx.model.Freunde;
import BoteFx.service.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;


/**
 * Den 6.11.2022
 */

@Controller
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FreundeCellController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    private MethodenService methodenService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private FreundeController freundeController;
    @Autowired
    private LayoutService layoutService;
    @Autowired
    private ChatBoxController chatBoxController;
    @Autowired
    private ApiService apiService;
    @Autowired
    private NeuemessageService countmessageService;

    @FXML private AnchorPane freundeCellAnchorPane;
    @FXML private Pane deleteCellPane;
    @FXML private ImageView deleteCellImg;
    @FXML private AnchorPane cellAnchorPane;
    @FXML private Label freundCellBild;
    @FXML private Label freundeCellName;
    @FXML private ImageView freundeCellHakenGreen;
    @FXML private ImageView freundeCellHakenGrau;
    @FXML private Label freundeCellDatum;
    @FXML private Label freundeCellMessage;
    @FXML private Label freundeCellMessageCount;
    @FXML private Button freundeCellButton;
    @FXML private VBox freundeInfo;

    private String freundHintergrund;
    private int count = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // User-Bild Hintergrund festlegen
        freundHintergrund = methodenService.zufallColor();
    }

    /**
     * Setter & Getter von rightPane(ChatBoxController/chatbox.fxml)
     * wird benutzt für den Chat Laden Zeile: 71 Methode: chatLaden()
     */
    private StackPane rechtePane;
    public StackPane getRechtePane() {
        return rechtePane;
    }
    public void setRechtePane(StackPane rechtepane) {
        this.rechtePane = rechtepane;
    }



    /**
     *      SETTER von setProperties
     *
     * friend: ist einen array(Alle Freunde) von Datenbank: mySql
     * http://localhost:8888/phpMyAdmin5/index.php?route=/database/structure&server=1&db=globalBote
     * zugesendet von FreundeController/freundeLaden() Zeile: 88
     *
     * a. delete Button(Chat-Freund Löschen), auf ID wird einen token gespeichert,
     *    für die Methode: freundRemove(), hier unten Zeile: ???
     *
     * b. Freunde Bild wird von Bote geladen
     *    Adresse: z.b.s = http:// localhost:8080/profilbild/03052022103644.png
     *    keinen Bild: wird nur Pseudonym ausgegeben in runden Label mit
     *    zufall color...ConfigService: zufallColor()...
     *
     * c. wenn die Name nicht vorhanden ist, wird pseudonym angezeigt
     *
     * d. Nachricht gelesen/ungelesen.... *****NOCH NICHT GEMACHT
     *
     * e. Datum gekürzt auf 10 ziffern, striche(-) umgetauscht auf punkt (.)
     *
     * f. kurze anzeige von der letzten Nachricht... *****NOCH NICHT GEMACHT
     *
     * g. insgesamt alee neue Nachrichten anzeigen... ******NOCH NICHT GEMACHT
     *
     * h. Chat-Start Button* mit der Methode chatLaden(), Zeile: 150
     *    *der Button ist auf die ganze AnchorPane aufgelegt außer den Löschen-Button
     *    style: fx:id="freundeCellButton" onAction="#chatLaden" opacity="0.0", <Font size="1.0" />
     *    ID mit Value belegt:  setId(friend.getMessagetoken());
     */
    private Freunde friend;

    public String getMessageToken(){
        return friend.getMessagetoken();
    }
    public String getFruendToken(){
        return friend.getFreundetoken();
    }

    public void setProperties(Freunde frienden) {
        this.friend = frienden;

    // a. delete button
        //friend.getMessagetoken();
        //deleteCellImg.setId(friend.getMessagetoken());


    // b. Freunde Bild
        String  freundBild  = friend.getFreundebild();
        String  freundPseu  = friend.getFreundepseudonym();

        if (freundBild.isBlank()){
            // kein Bild vorhanden
            freundCellBild.setStyle("-fx-background-radius: 25;"+"-fx-background-color:"+ freundHintergrund +";" +
            "-fx-font-family: \"Sans-Serif\"; -fx-font-weight: 700; -fx-text-fill: white;");
            freundCellBild.setText(freundPseu);
        } else {
            // Bild aus mysql(Bote) holen
            Image freundImg = new Image(configService.FILE_HTTP+"profilbild/"+freundBild+".png", true);
            ImageView imageView = new ImageView(freundImg);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            freundCellBild.setGraphic(imageView);
        }


    // c. Name oder Pseudonym Ausgeben
        String freundName = friend.getFreundename();
        if (freundName.isBlank()){
            // wenn keine Namen vorhanden ist
            freundeCellName.setText(friend.getFreundepseudonym());
        } else {
            freundeCellName.setText(friend.getFreundevorname()+" "+ friend.getFreundename());
        }


    // d. Nachricht gelesen oder ungelesen
        /**
         * noch nicht vorgesehen
         */



    // e. Datum von letzte Nachricht
        if (friend.getDatumLetzteNachricht() != null) {

            String kurzeDatum = friend.getDatumLetzteNachricht().substring(0,8);
            freundeCellDatum.setText(kurzeDatum);
        } else {
            freundeCellDatum.setText("--");
        }


    // f. kurze anzeige von der letzten Nachricht
        freundeCellMessage.setText(friend.getLetzteNachricht());


    // g. Meldung von der neuen Nachricht anzeigen

        String h2Count = loadCountNeueNachricht();
        if (h2Count != null){
            count = Integer.parseInt(h2Count);
            aktualisiereMessageCount();
        } else {
            // h2count ist null
        }
        //freundeCellMessageCount.setText("99");


    // h.
        freundeCellButton.setId(String.valueOf(friend.getMessagetoken()));
        //System.out.println("Set Property: " + freundeCellButton);


    // j.
        // Anzeige von Eingeladenen/
        String einladenInfo = friend.getRole();
        switch (einladenInfo){
            case "wartenaufok":         wartetAufOK();
                                        break;
            case "werdeeingeladen":     werdeEingeladen();
                                        break;
            default:                    break;
        }

    } // Ende setProperties



    /**
     * 2 Methoden zur Chat-Einladung
     *
     * 1. Methode: wartenAufOk...
     *  nur Information, dass Freundschaft nicht angenommen ist
     *
     *  2. Methode: werdeEingeladen
     *  Freundschaft annehmen oder ablehnen
     */
    private void wartetAufOK(){

        Label text = new Label("Freundschaftliche Einladung an " + friend.getFreundepseudonym() );
        text.getStyleClass().add("cellText");

        Label adresse = new Label(friend.getFreundemail() != null ? "\u0028"+friend.getFreundemail()+"\u0029" : "\u0028"+friend.getFreundetelefon()+"\u0029");
        adresse.getStyleClass().add("cellAdresse");

        Label info = new Label("Warten, noch nicht angenommen!");
        info.getStyleClass().add("cellInfo");

        freundeInfo.setVisible(true);
        freundeInfo.setStyle("-fx-background-color: #E6FFE6;");
        freundeInfo.getChildren().addAll(text, adresse, info);

    }
    private void werdeEingeladen(){
        Label text = new Label("Freundschaftliche Einladung von " +friend.getFreundepseudonym());
        text.getStyleClass().add("cellText");

        Label adresse = new Label(friend.getFreundemail() != null ? "\u0028"+friend.getFreundemail()+"\u0029" : "\u0028"+friend.getFreundetelefon()+"\u0029");
        adresse.getStyleClass().add("cellAdresse");

        // Buttons Eigenschaften
        HBox hBox = new HBox();
        Button nein = new Button("Ablehnen");
        nein.getStyleClass().add("cellNein");
        nein.prefWidthProperty().bind(hBox.widthProperty());

        Button ja = new Button("Annehmen");
        ja.getStyleClass().add("cellJa");
        ja.prefWidthProperty().bind(hBox.widthProperty());
        hBox.getChildren().addAll(nein, ja);

        // Click auf den dynamischen Button (ja oder nein)
        boolean ok = true;
        boolean no = false;
        nein.setOnAction(event ->{
            einladungErlaubnis(friend.getMessagetoken(), no);
        });
        ja.setOnAction(event -> {
            einladungErlaubnis(friend.getMessagetoken(), ok);
        });

        freundeInfo.setVisible(true);
        freundeInfo.setStyle("-fx-background-color: #E6FFE6;");
        freundeInfo.getChildren().addAll(text, adresse, hBox);

    }



    /**
     * Methode für die Freundschaft annehmen oder Ablehnen
     * zugesendet zu Bote: ApiEinladenController Zeile: 200
     * 'messageToken' und 'true' oder 'false'
     *
     * response: true(angenommen) oder false(abgelehnt)
     */
    private void einladungErlaubnis(String mesageToken, boolean jein){
        // Request an Bote & response
        String url  = configService.FILE_HTTP+"einladungPrufenApi";
        String json = "{\"messToken\":\""+mesageToken+"\", \"erlaubt\":\""+jein+"\"}";
        HttpResponse<String> response = apiService.requestAPI(url, json);
        boolean output = Boolean.parseBoolean(response.body());

        if (output){
            freundeInfo.getChildren().clear();
            ((Pane) freundeInfo.getParent()).getChildren().remove(freundeInfo);
        } else {
            // output false: nichts machen
            freundeInfo.getChildren().clear();
        }

    }



    /* ************** Click auf dem Chat-Freund / Chat Laden + Freunde Löschen ***************** */


    /**
     *  CLICK AUF DIE FREUNDE-OBERFLÄCHE...
     *
     *  1. Prüfen mit 'Bounds' ob Freunde-Delete-Button angezeigt wird, wenn ja,
     *  dann mit der Methode: freundeController.freundeRemoveSchliessen(); Bilder austauschen
     *  runde mit Kreuz verstecken, männchen mit minus anzeigen
     *  und die remove-Pane schliessen
     *
     *  2. Chat Laden: anzeige in der rightPane
     *
     *  3. Freunde-Fläche hover setzen
     */
    @FXML
    public void cellClick() {

        /* 1. */
        if (paneONPrufen() == 0.0) {
            freundeController.freundeRemoveSchliessen();
        }

        /* 2. */
        chatLaden();

        /* 3. */
        freundeController.freundeHover(this);
        resetEmpfangeneNachrichten();
    }



    /**
     * CHAT LADEN
     * messagesToken, wird als Button-ID in setProperties() gesetzt Zeile: 153(hier)
     * "openmessage" = functioniert, wenn Stage unter 650px verkleinert wird,
     * ChatBoxController/changedPane()  Zeile: 330
     *
     * //@param event
     */
    private void chatLaden() {

        String messagesToken = freundeCellButton.getId();

        if (messagesToken.isBlank()){
            layoutService.setausgabeLayout(rechtePane);
            layoutService.switchLayout(GlobalView.MESSAGELEER);

        } else {
            //rechtePane.getChildren().clear();
            layoutService.setausgabeLayout(rechtePane);
            MessageController messageController = (MessageController) layoutService.switchLayout(GlobalView.MESSAGE);
            //messageController.setRechtsPane(rechtePane);
            messageController.setFreundColor(freundHintergrund);
            messageController.setChatFreundeDaten(friend);
            messageController.setMessageToken(messagesToken);
            messageController.setCelleArchorPane(cellAnchorPane);

            chatBoxController.setMessageController(messageController);

            //System.out.println("Freunde Cell Controller Zeile: 320 " + messagesToken);
        }

        // Translate Starten (functioniert nur unter 650px)
        translate.offenStackPane();
    }



    /**
     * 2 Methoden für den Hover effect,
     * nur an Freunde-Cell, kein Setting-Hover effect
     */
    public void resetHover() {
        cellAnchorPane.getStyleClass().remove("freundeAktiv");
    }
    public void setHover() {
        cellAnchorPane.getStyleClass().add("freundeAktiv");
    }



    /**
     * Delete Pane prüfen auf offen oder zu ist
     * return: 0.0 = zeigt der roten button an
     * return: -25.0 = die Löschen pane ist ausgeblendet() standard position
     *
     * @return
     */
    public double paneONPrufen(){
        Bounds bonds = deleteCellPane.localToParent(deleteCellPane.getBoundsInLocal());
        return bonds.getMinX();
    }



    /**
     *  NUR, FÜR DIE ANZEIGE/VERBERGEN VON DEM LÖSCHEN-BUTTON(roter runde buttons)
     *  ***die löschen von Freund wird in freundRemove() durchgeführt***
     *
     * die Methode wird in FreundeController gestartet, void removePaneZeigen(boolean zeige)
     * Zeile: 250, 252
     */
    public void paneRemoveZeigen(boolean showDeletePane){

        if (showDeletePane){
            // deletePane Anzeigen
            AnchorPane.setLeftAnchor(cellAnchorPane, 25.0);
            AnchorPane.setLeftAnchor(deleteCellPane, 0.0);
        } else {
            // deletePane verstecken
            AnchorPane.setLeftAnchor(cellAnchorPane, 0.0);
            AnchorPane.setLeftAnchor(deleteCellPane, -25.0);
        }

    }



    /**
     * Click auf dem roten Löschen-Button
     * eine messageToken wird als parameter an Bote/apiFreundeController
     * ...@PostMapping(value = "/freundeRemoveApi") gesendet, da werden alle
     * einträge in der tabelle freunde & meassages unwiederruflich gelöscht...
     * weiter info: Bote/apiFreundeController
     *
     * zurück gesendete daten von Bote sind in 3 verschiedene arten zu empfangen
     *  1. response,                    ausgabe: (POST http://localhost:8080/freundeRemoveApi) 200 (sendet immer)
     *  2. response.body(),             ausgabe: 0 (nicht gelöscht, kein messageToken, oder was anderes)
     *  3. response.body(),             ausgabe: 2 (ich & freund werden gelöscht)
     *  4. gibst noch eine möglichkeit die Daten von mir & Freund
     *     als json-array response zu bekommen, mehr information in
     *     Bote/ApiFreundeController Zeile: ab 70, muss nur return ändern
     *     z.b.s return ResponseEntity.status(HttpStatus.OK).body(geloschteFreund);
     */
    public void freundRemove() {

        // Request & response an/von Bote
        String messagetoken = friend.getMessagetoken();
        String removeUrl = configService.FILE_HTTP+"freundeRemoveApi";
        String jsonMessageToken = "{ \"sendmessagetoken\":\""+ messagetoken +"\" }";
        HttpResponse<String> response = apiService.requestAPI(removeUrl, jsonMessageToken);
        int output = Integer.parseInt(response.body());

        if (output >= 2){

            int timer = 5;
            methodenService.hidePane(freundeCellAnchorPane, timer);

        } else {
            // nicht machen, oder globaleFehler anlegen
        }
    }


    /* ************** neue eingehende Nachrichten + count in H2(Local) speichern  **************** */


    /**
     *  beim Eingehen eine neue Nachricht count wird erhöht und
     *  angezeigt + h2 Datenbank speicherung
     */
    public void nachrichtEmpfangen() {

        count++;
        aktualisiereMessageCount();
        saveCountNeueNachricht();
    }


    /**
     * zeigt die anzahl die neuen Nachrichten an...
     */
    private void aktualisiereMessageCount() {

        if (count < 1) {

            freundeCellMessageCount.setText("");
            freundeCellMessageCount.getStyleClass().remove("freundeCellCount");
            return;

        }

        freundeCellMessageCount.setText(String.valueOf(count));
        freundeCellMessageCount.getStyleClass().add("freundeCellCount");
    }


    /**
     * count reset + H2 Datenbank löschung von eingehende Nachrichten,
     * Resetet wird über den Click auf den Freund Oberfläche Zeile: 317
     *
     * ACHTUNG: wird ausgelöst, wenn neue nachricht vorhanden sind(count großer als null)
     */
    private void resetEmpfangeneNachrichten() {

        if (count > 0){

            count = 0;
            aktualisiereMessageCount();
            // count von neue Nachricht in H2 Löschen
            deleteCountNeueNachricht();
        }

    }


    /**
     * User nicht Online: neue eingehende Nachrichten ins H2 datenbank speichern
     * Parameter: Freunde Token(wer nachricht versendet)
     *            count, alle eingehende nachrichten in der Methode 'nachrichtEmpfangen' hochzählen
     */
    private void saveCountNeueNachricht(){

        countmessageService.saveCountNeueMessage(friend.getFreundetoken(), String.valueOf(count));
    }


    /**
     * prüfen, ob neue nachricht eingegangen sind: Zeile: 188
     *
     * return: Zahl oder null
     * @return
     */
    private String loadCountNeueNachricht(){

        return countmessageService.loadCountNeueMessage(friend.getFreundetoken());

    }


    /**
     * mit dem Klick auf die Freunde oberfläche werde der eintrag in H2 Datenbank gelöscht
     * Parameter: freunde token
     * output: true oder false
     */
    private void deleteCountNeueNachricht(){

        boolean countOutput = countmessageService.deleteCountNeueMessage(friend.getFreundetoken());
        //System.out.println("Count von neue Nachricht wurde gelöscht: " + countOutput);

    }


}

