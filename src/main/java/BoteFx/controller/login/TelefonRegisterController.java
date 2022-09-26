package BoteFx.controller.login;

import BoteFx.configuration.GlobalApiRequest;
import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.model.Token;
import BoteFx.service.TokenService;
import BoteFx.service.ViewService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

/**
 * Den 24.09.2022
 */

@Controller
public class TelefonRegisterController implements Initializable {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());
    @Autowired
    private ViewService viewService;
    @Autowired
    private TokenService tokenService;

    @FXML private VBox telefonRegisterHauptVBox;
    @FXML private AnchorPane telefonRegisterAnchorPane;
    @FXML private Label telefonRegisterFehler;
    @FXML private Label telefonRegisterNummer;
    @FXML private TextField kodeEins;
    @FXML private TextField kodeZwei;
    @FXML private TextField kodeDrei;
    @FXML private TextField kodeVier;
    @FXML private Button telefonRegisterButton;

    /* zum spätern löschen */
    @FXML private String testCode;
    /* zum spätern löschen */

    /* Getter & Setter von Telefon Login Controller */
    private String loginTelefon;
    public String getLoginTelefon(){ return loginTelefon; }
    public void setLoginTelefon(String telefonZugesandt){
        this.loginTelefon = telefonZugesandt;
        /**
         * Telefonnummer ist zugesendet von TelefonLoginController Zeile: 104
         * QUELLE: telefonRegisterController.setLoginTelefon(tel);
         */
        JSONObject object = new JSONObject(loginTelefon);
        String telMitPlus = (String) object.get("telefonMitPlus");
        telefonRegisterNummer.setText(telMitPlus);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Stage auf 100% Heught ziehen */
        telefonRegisterAnchorPane.prefHeightProperty().bind(telefonRegisterHauptVBox.heightProperty());

        /* Telefon Information Ausgeben, hier unten in Fehler Ausgabe */
        telefonRegisterFehlerAusgabe("telefonsendeninfo", "ok");
    }


    /**
     * Aktivierung Code prüfen & weiter
     * an Bote -> ApiMailController.java -> @PostMapping(value = "/kodeApi") senden
     *
     * HttpResponse response (x2) mal:
     *  1. response als status: response.statusCode() == 200
     *  2. response als json:   response.body() ( Daten als json-format )
     *
     * @param event
     */
    public void kodeRequest(ActionEvent event) {
        String kode1 = kodeEins.getText();
        String kode2 = kodeZwei.getText();
        String kode3 = kodeDrei.getText();
        String kode4 = kodeVier.getText();
        if (kode1.isEmpty() || kode2.isEmpty() || kode3.isEmpty() || kode4.isEmpty()){

            kodeVier.setText("");
            kodeDrei.setText("");
            kodeZwei.setText("");
            kodeEins.setText("");
            kodeEins.requestFocus();
            telefonRegisterFehlerAusgabe("feldleer","no");
            return;
        }

        /* Daten für request sammeln: Bote -> ApiMailController.java Methode: @PostMapping(value = "/codeApi") */
        JSONObject obj = new JSONObject(getLoginTelefon());
        String telOnePlus = (String) obj.get("telefonOnePlus");
        String kode = String.valueOf(Integer.parseInt(kode1 + "" + kode2 + "" + kode3 + "" + kode4));
        String urlApi = GlobalConfig.FILE_HTTP+"kodeApi";
        String jsonApi = "{\"kode\":\""+kode+"\", \"telefon\":\""+telOnePlus+"\"}";

        /* request & response */
        HttpResponse<String> response = GlobalApiRequest.requestAPI(urlApi, jsonApi);
        if (response != null && response.statusCode() == 200) {

            /* token ermitteln von response */
            JSONObject objec = new JSONObject(response.body());
            String resToken = objec.getString("tokenResponse");

            /* token in H2 Database speichern (localBote/Token) */
            Token H2Token = tokenService.findeToken(resToken);     // h2token output: null
            if (H2Token == null){
                String datum = GlobalConfig.deDatum();
                Token neuToken = new Token();
                neuToken.setDatum(datum);
                neuToken.setMytoken(resToken);
                tokenService.saveToken(neuToken);

            } else {
                telefonRegisterFehlerAusgabe("H2NoSpeichern", "no");
            }

            /* token in json Datei schreiben */
            String txttoken = tokenService.writeToken(resToken);
            switch (txttoken){
                case "writeJsonOk":     TelefonSuccessController telefonSuccessController = (TelefonSuccessController) viewService.switchTo(GlobalView.TELEFONSUCCESS);
                                        telefonSuccessController.setUserDaten(response.body());
                                        break;
                case "writeJsonNein":   telefonRegisterFehlerAusgabe("writeJsonNo", "no");
                                        return;
                case "eintragExist"     :telefonRegisterFehlerAusgabe("eintragExistiert", "no");
                                        return;
                default:                telefonRegisterFehlerAusgabe("writeFehler", "no");
                                        return;
            }
            //System.out.println("IF Request: " + response.body());
        } else {
            kodeVier.setText("");
            kodeDrei.setText("");
            kodeZwei.setText("");
            kodeEins.setText("");
            kodeEins.requestFocus();

            /* Später Löschen */
            JSONObject obje = new JSONObject(response.body());
            testCode = String.valueOf(obje.getInt("testCode"));
            /* Ende später Löschen */

            System.out.println("TelefonRegisterController Zeile: 163  / " + testCode);
            telefonRegisterFehlerAusgabe("falscheCode", "no");
        }
    }

    /**
     * Code Validate
     *  1. auf Zahlen prüfen
     *  2. auf Length prüfen
     *  3. bei Eingeben der Zahl, Focus auf nächsten Feld setzen
     *
     * @param event
     */
    public void kodeValidate(KeyEvent event) {
        String id = ((Node) event.getSource()).getId();
        TextField txt = null;
        switch (id){
            case "kodeEins":    txt = kodeEins;
                break;
            case "kodeZwei":    txt = kodeZwei;
                break;
            case "kodeDrei":    txt = kodeDrei;
                break;
            case "kodeVier":    txt = kodeVier;
                break;
            default:            telefonRegisterFehlerAusgabe("kodevalidfehler","no");
                break;
        }

        TextField kodeTxt = txt;
        kodeTxt.textProperty().addListener((glVal, altVal, neuVal) -> {
            /* nur Zifern erlaubt */
            if (kodeTxt.getText().matches("[0-9]*")) {

                kodeTxt.setText(neuVal);

                /* bei Zahl Angabe Focus auf anderen Feld Übertragen */
                if (kodeTxt.getText().matches("[0-9]")){
                    switch (kodeTxt.getId()){
                        case "kodeEins":    kodeZwei.requestFocus();
                                            break;
                        case "kodeZwei":    kodeDrei.requestFocus();
                                            break;
                        case "kodeDrei":    kodeVier.requestFocus();
                                            break;
                        case "kodeVier":    kodeVier.isFocused();
                                            telefonRegisterButton.setDefaultButton(true);
                                            break;
                        default:            break;
                    }
                }
                //System.out.println("Nur Ziffern: " + kodeTxt.getId());

            } else {
                /* bei Buchstabe eingabe auf strich ändern, bei leer wird Fehler ausgelöst */
                kodeTxt.setText(altVal);
                telefonRegisterFehlerAusgabe("zahlNO", "no");
            }

            /* Länge auf Einer Zahl begrenzen */
            if (kodeTxt.getText().length() > 1) {
                String k = kodeTxt.getText().replaceAll("\\s+", "").substring(1, 2);
                if (!k.equals("")) {
                    kodeTxt.setText(k);
                    //System.out.println("Lenge: " +s);
                }
            }

        });
    }

    /**
     * Link zu Mail Login
     * @param event
     */
    public void telefonLoginLinks(ActionEvent event) {
        viewService.switchTo(GlobalView.MAILLOGIN);
    }

    /**
     * Stage Fenster schliessen
     * @param event
     */
    public void telefonRegisterClose(ActionEvent event) {
        GlobalConfig.stageClose(event);
    }

    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    public void telefonRegisterDragged(MouseEvent event) {
        GlobalConfig.dragget(event);
    }
    public void telefonRegisterPressed(MouseEvent event) {
        GlobalConfig.pressed(event);
    }



    /**
     * Allgemeine Fehler Ausgabe
     */
    @Value("${telefonsenderinfo}")
    private String telefonSendenInfo;
    public void telefonRegisterFehlerAusgabe(String text, String ok){
        // mailRegisterFehler.setTextFill(ok == "ok" ? Color.GREEN : Color.RED);
        telefonRegisterFehler.getStyleClass().clear();
        telefonRegisterFehler.getStyleClass().add(ok == "ok" ? "mailfehlerOK" : "mailfehlerNO");
        switch (text){
            case "telefonsendeninfo":       telefonRegisterFehler.setText(telefonSendenInfo);
                                            break;
            case "kodevalidfehler":         telefonRegisterFehler.setText("ERROR: allgemeine Fehler bei Methode codeValidate");
                                            break;
            case "feldleer":                telefonRegisterFehler.setText("Feld ausfüllen");
                                            break;
            case "zahlNO":                  telefonRegisterFehler.setText("nur Zahlen sind Erlaubt");
                                            break;
            case "falscheCode":             telefonRegisterFehler.setText("Prüfen Sie, dass Sie den Aktivierungscode richtig eingegeben haben. \n" +
                                            "die richtige Code ist: " + testCode);
                                            break;
            case "H2NoSpeichern":           telefonRegisterFehler.setText("H2 Database kann nicht gespeichert werden, Zeile: 135");
                                            break;
            case "writeJsonNo":             telefonRegisterFehler.setText("token.json datei kann nicht beschrieben werden");
                                            break;
            case "eintragExistiert":        telefonRegisterFehler.setText("token.json: Eintrag existiert schon");
                                            break;
            case "writeFehler":             telefonRegisterFehler.setText("token.json: Allgemeine write Fehler");
                                            break;
            default:                        break;
        }
    }

}
