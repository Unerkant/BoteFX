package BoteFx.controller.login;

import BoteFx.service.*;
import BoteFx.Enums.GlobalView;
import BoteFx.model.Token;

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

    @Autowired
    private ViewService viewService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private MethodenService methodenService;

    @FXML private VBox mailRegisterHauptVBox;
    @FXML private AnchorPane mailLRegisterAnchorPane;
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

        // Information zu verschickte E-Mail
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
     * HttpResponse response (x2) mal:
     *  1. response als status: response.statusCode() == 200
     *  2. response als json:   response.body()
     *
     * @param event
     */
    @FXML
    public void codeRequest(ActionEvent event) {

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
        String apiUrl = configService.FILE_HTTP+"codeApi";
        String apiMail = mailRegisterEmail.getText();
        String apiJson = "{\"code\":"+code+", \"mail\":"+apiMail+"}";
        /* request & response */
        HttpResponse<String> response = apiService.requestAPI(apiUrl, apiJson);


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

            String tokenOK = null;

            /* token ermitteln von response */
            JSONObject object = new JSONObject(response.body());
            String token = object.getString("tokenOutput");

            /* token in H2 Database speichern (localBote/Token) */
            String h2token = tokenService.meinToken();     // h2token output: null
            if (h2token == null){
                Token newToken = new Token();
                newToken.setMytoken(token);
                tokenService.saveToken(newToken);
                tokenOK = "writeH2OK";
            } else {
                mailRegisterFehlerAusgabe("h2speichernNo", "no");
            }

            switch (tokenOK){
                case "writeH2OK":     MailSuccessController mailSuccessController = (MailSuccessController) viewService.switchTo(GlobalView.MAILSUCCESS);
                                        mailSuccessController.setUserDaten(response.body());
                                        break;
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
            System.out.println("MailRegisterController Zeile: 173  / " + testerCode);
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
    public void codeValidate(KeyEvent event) {
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
               codeTxf.setText(alt);
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
        methodenService.pressed(event);
    }


    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    @FXML
    public void mailRegisterDragged(MouseEvent event) {
        methodenService.dragget(event);
    }


    /**
     * Wechseln zu maillogin.fxml
     * @param event
     */
    @FXML
    public void mailLoginLinks(ActionEvent event) {
        viewService.switchTo(GlobalView.MAILLOGIN);
    }


    /**
     * Stage Schliessen
     * @param event
     */
    @FXML
    public void mailRegisterClose(ActionEvent event) {
        methodenService.stageClose(event);
    }


    /**
     * Allgemeine Fehler Ausgabe
     *
     * @param text
     * @param ok
     */
    @Value("${mailsenderinfo}")
    private String mailSenderInfo;


    /**
     * Allgemeine Fehler Ausgabe
     * @param text
     * @param ok
     */
    public void mailRegisterFehlerAusgabe(String text, String ok){

       // mailRegisterFehler.setTextFill(ok == "ok" ? Color.GREEN : Color.RED);
        mailRegisterFehler.getStyleClass().clear();
        mailRegisterFehler.getStyleClass().add(ok == "ok" ? "mailfehlerOK" : "mailfehlerNO");

        switch (text){
            case "mailsenderinfo":  mailRegisterFehler.setText(mailSenderInfo);
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
