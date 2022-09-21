package BoteFx.controller.login;

import BoteFx.configuration.GlobalApiRequest;
import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.model.Token;
import BoteFx.service.TokenService;
import BoteFx.service.ViewService;

import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;


/**
 * Den 25.08.2022
 */
@Controller
public class MailRegisterController implements Initializable {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;
    @Autowired
    private TokenService tokenService;

    @FXML private VBox mailRegisterHauptVBox;
    @FXML private AnchorPane mailLRegisterAnchorPane;
    @FXML private Label mailRegisterTitle;
    @FXML private VBox mailRegisterVBox;
    @FXML private Label mailRegisterFehler;
    @FXML  private Label mailRegisterEmail;
    @FXML private TextField codeEins;
    @FXML private TextField codeZwei;
    @FXML private TextField codeDrei;
    @FXML private TextField codeVier;
    @FXML private Button mailRegisterButton;

    /* Zum Spätern Löschen */
    @FXML public String testerCode;
    /* Ende zum Löschen */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Mail Register Height auf 100% ziehen
        mailLRegisterAnchorPane.prefHeightProperty().bind(mailRegisterHauptVBox.heightProperty());

        // Information zu Verschickte E-Mail
        mailRegisterFehlerAusgabe("mailsenderinfo", "ok");

    }

    /**
     * Getter & Setter von E-Mail
     *
     * Quelle: MailLoginController.java Zeile 88
     * maillogin.fxml -> TextField #mailLoginInput
     *
     * die E-mail wurde aus dem TextField #mailLoginInput (maillogin.fxml) gelesen &
     * an den Label #mailRegisterEmail (mailregister.fxml) weiter gegeben
     */
    private String loginEmail;
    public String getLoginEmail(){ return loginEmail; }
    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
        mailRegisterEmail.setText(loginEmail);
    }


    /**
     * Aktivierung Code prüfen & weiter
     * an Bote -> ApiMailController.java -> @PostMapping(value = "/codeApi") senden
     *
     * HttpResponse response (x2):
     *  1. response als status: response.statusCode() == 200
     *  2. response als json:   response.body()
     *
     * @param event
     */
    @FXML
    public void codePrufen(ActionEvent event) {

        String code1 = codeEins.getText();
        String code2 = codeZwei.getText();
        String code3 = codeDrei.getText();
        String code4 = codeVier.getText();
        if (code1.isEmpty() || code2.isEmpty() || code3.isEmpty() || code4.isEmpty()){

            codeVier.setText("");
            codeDrei.setText("");
            codeZwei.setText("");
            codeEins.setText("");
            codeEins.requestFocus();
            mailRegisterFehlerAusgabe("feldleer","no");
            return;
        }

        /* Daten für request sammeln: Bote -> ApiMailController.java Methode: @PostMapping(value = "/codeApi") */
        String code = String.valueOf(Integer.parseInt(code1 + "" + code2 + "" + code3 + "" + code4));
        String apiUrl = GlobalConfig.FILE_HTTP+"codeApi";
        String apiMail = mailRegisterEmail.getText();
        String apiJson = "{\"code\":"+code+", \"mail\":"+apiMail+"}";

        /* request & response */
        HttpResponse<String> response = GlobalApiRequest.requestAPI(apiUrl, apiJson);

        /**
         *  Response von Bote -> ApiMailController.java Methode: @PostMapping(value = "/codeApi")
         *  der json-array wird von Bote -> ApiMailController -> return ResponseEntity.status(HttpStatus.OK).body(userResponse);
         *  Zeile:155 zugesendet & schließlich an der MailSuccessController weiter geleitet(hier gleich unten)
         *   1. response als response.statusCode() == 200
         *   2. response als response.body()...(json-object, Alle Daten von User)
         *
         *   3. token in einem json-datei sichern: resources/static/json/token.json
         *      zum weitere nutzung als 'Cookie' in BoteApp.java
         *   4. token & datum in H2 localBote/Token speichern
         */
        if (response != null && response.statusCode() == 200){

            /* token ermitteln von response */
            JSONObject object = new JSONObject(response.body());
            String token = object.getString("tokenOutput");

            /* token in H2 Database speichern (localBote/Token) */
            Token h2token = tokenService.findeToken(token);     // h2token output: null
            if (h2token == null){
                String datum = GlobalConfig.deDatum();
                Token newToken = new Token();
                newToken.setDatum(datum);
                newToken.setMytoken(token);
                tokenService.saveToken(newToken);

            } else {
                mailRegisterFehlerAusgabe("h2speichernNo", "no");
            }

            /* token in json Datei schreiben */
            String txttoken = tokenService.writeToken(token);
            switch (txttoken){
                case "writeJsonOk":     MailSuccessController mailSuccessController = (MailSuccessController) viewService.switchTo(GlobalView.MAILSUCCESS);
                                        mailSuccessController.setUserDaten(response.body());
                                        break;
                case "writeJsonNo":     mailRegisterFehlerAusgabe(txttoken, "no");
                                        return;
                case "eintragExistiert":mailRegisterFehlerAusgabe(txttoken, "no");
                                        return;
                default:                mailRegisterFehlerAusgabe("writeFehler", "no");
                                        return;
            }

        } else {
            codeVier.setText("");
            codeDrei.setText("");
            codeZwei.setText("");
            codeEins.setText("");
            codeEins.requestFocus();

            /* Später Löschen */
            JSONObject object = new JSONObject(response.body());
            testerCode = String.valueOf(object.getInt("testerCode"));
            /* Ende später Löschen */

            mailRegisterFehlerAusgabe("codefalsch", "no");
        }

    }


    /**
     * Validate Aktivierung Code
     * Erlaubt sind nur Zahlen
     * bei Zahl eingabe wird der Focus Automatisch an nächsten Feld Übertragen
     *
     * @param event
     */
   @FXML
    public void codeEingabe(KeyEvent event) {
        String id = ((Node) event.getSource()).getId();
        TextField txf = null;
        switch (id){
            case "codeEins":    txf = codeEins;
                                break;
            case "codeZwei":    txf = codeZwei;
                                break;
            case "codeDrei":    txf = codeDrei;
                                break;
            case "codeVier":    txf = codeVier;
                                break;
            default:            mailRegisterFehlerAusgabe("codevalidfehler","no");
                break;
        }

       TextField codeTxf = txf;
       codeTxf.textProperty().addListener((gl, alt, neu) -> {
           /* nur Zifern erlaubt */
           if (codeTxf.getText().matches("[0-9-]*")) {

               codeTxf.setText(neu);

               /* bei Zahl Angabe Focus auf anderen Feld Übertragen */
               if (codeTxf.getText().matches("[0-9]")){
                   switch (codeTxf.getId()){
                       case "codeEins":     codeZwei.requestFocus(); break;
                       case "codeZwei":     codeDrei.requestFocus(); break;
                       case "codeDrei":     codeVier.requestFocus(); break;
                       case "codeVier":     codeVier.isFocused();
                                            mailRegisterButton.setDefaultButton(true);
                                            break;
                       default:             break;
                   }
               }
               //System.out.println("Nur Ziffern: " + codeTxf.getId());

           } else {
               /* bei Buchstabe eingabe auf strich ändern, bei leer wird Fehler ausgelöst */
               codeTxf.setText("-");
               mailRegisterFehlerAusgabe("zahlNO", "no");
           }

           /* Länge auf Einer Zahl begrenzen */
           if (codeTxf.getText().length() > 1) {
               String s = codeTxf.getText().replaceAll("\\s+", "").substring(1, 2);
               if (!s.equals("")) {
                   codeTxf.setText(s);
                   //System.out.println("Lenge: " +s);
               }
           }

       });
    }



    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    @FXML
    public void mailRegisterPressed(MouseEvent event) {
        GlobalConfig.pressed(event);
    }

    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    @FXML
    public void mailRegisterDragged(MouseEvent event) {
        GlobalConfig.dragget(event);
    }

    /**
     * Wechseln zu maillogin.fxml
     * @param event
     */
    @FXML
    public void mailLoginLinks(ActionEvent event) {
        viewService.switchTo(GlobalView.LOGINMAIL);
    }

    /**
     * Stage Schliessen
     * @param event
     */
    @FXML
    public void mailRegisterClose(ActionEvent event) {
        GlobalConfig.stageClose(event);
    }

    /**
     * Algemeine Fehler Ausgabe
     *
     * @param text
     * @param ok
     */
    @Value("${senderinfo}")
    private String senderInfo;

    public void mailRegisterFehlerAusgabe(String text, String ok){

       // mailRegisterFehler.setTextFill(ok == "ok" ? Color.GREEN : Color.RED);
        mailRegisterFehler.getStyleClass().clear();
        mailRegisterFehler.getStyleClass().add(ok == "ok" ? "mailfehlerOK" : "mailfehlerNO");

        switch (text){
            case "mailsenderinfo":  mailRegisterFehler.setText(senderInfo);
                                    break;
            case "feldleer":        mailRegisterFehler.setText("Feld ausfüllen");
                                    break;
            case "codevalidfehler": mailRegisterFehler.setText("ERROR: allgemeine Fehler bei Methode codeValidate");
                                    break;
            case "zahlNO":          mailRegisterFehler.setText("nur Zahlen sind Erlaubt");
                                    break;
            case "codefalsch":      mailRegisterFehler.setText("Prüfen Sie, dass Sie den Aktivierungscode richtig eingegeben haben. \n" +
                                    "die richtige Code ist: " + testerCode);
                                    break;
            case "h2speichernNo":   mailRegisterFehler.setText("H2 Database kann nicht gespeichert werden, Zeile: 150");
                                    break;
            case "writeJsonNo":     mailRegisterFehler.setText("token.json datei kann nicht beschrieben werden");
                                    break;
            case "eintragExistiert":mailRegisterFehler.setText("token.json: Eintrag existiert schon");
                                    break;
            case "writeFehler":     mailRegisterFehler.setText("token.json: Allgemeine write Fehler");
                                    break;
            default:                break;
        }

        /*   AKTUELL AUSGESETZT 21.9.2022  */
        /* Automatische Ausblenden nach 5 Sekunden */
        /*PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> mailRegisterFehler.setText(null));
        pause.play();*/
    }

}
