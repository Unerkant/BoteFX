package BoteFx.controller.login;

import BoteFx.configuration.GlobalApiRequest;
import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

@Controller
public class TelefonLoginController implements Initializable {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());
    @Autowired
    private ViewService viewService;

    @FXML private VBox telefonLoginHauptVBox;
    @FXML private AnchorPane telefonLoginAnchorPane;
    @FXML private Label telefonLoginFehler;
    @FXML private Label telefonLoginInfo;
    @Value("${telefonlogininfo}")
    private String telefonlogininfo;
    @FXML private TextField vorwahlInput;
    @FXML private TextField telefonInput;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* Stage auf 100% height ziehen */
        telefonLoginAnchorPane.prefHeightProperty().bind(telefonLoginHauptVBox.heightProperty());

        /**
         *  Telefon Login Information
         *  änderung der Textes in applications.properties vornehmen
         */
        telefonLoginInfo.setText(telefonlogininfo);

        /**
         *  Automatische Internationale Language erkennen
         *
         *  änderungen in GlobalConfig Zeile: 90 vornehmen
         */
        String language = System.getProperty("user.language");
        String land = GlobalConfig.languagen(language);
        vorwahlInput.setText(land);

    }

    /**
     * Request ab Bote/ApiTelefonController/@PostMapping(value = "/telefonApi")
     * Zeile: 55
     *
     * @param event
     */
    public void telefonRequest(ActionEvent event) {

        /**
         * Daten Sammeln
         * String tel:      Telefonnummer mit Plus & LeerZeichen zwischen vorwahl und Telefon Anbieter
         *                  benutzt hier Zeile 103 und TelefonRegisterController/ Zeile 53, (Setter/Getter)
         *                  +49 175 1234567( für Anzeige )
         * String telefon: Telefonnummer ohne Plus(+) & Leerzeichen(" "), Internationale ausführung
         *                 491751234567( für SMS senden )
         */
        String tel = vorwahlInput.getText()+" "+telefonInput.getText().substring(0, 3)+" "+telefonInput.getText().substring(3);
        String telefon = tel.replace("+", "").replace(" ", "");
        String link = GlobalConfig.FILE_HTTP+"telefonApi";
        String data = "{\"neuUserTelefon\":\""+telefon+"\"}";
        String zuRegisterSenden = "{\"telefonMitPlus\":\""+tel+"\", \"telefonOnePlus\":\""+telefon+"\"}";

        /**
         * Telefon gesendet zu Bote/ApiTelefonController/@PostMapping(value = "/telefonApi")
         *
         * response: 200 oder 404/500
         *
         * zu Testen Telefon: +44 7520635797
         * URL: https://sms-online.co/de/kostenlos-sms-empfangen/447520635797
         * VORAUSSETZUNG: haben Sie genug 'Text Credits' bei Englischen arbiter
         * die MSM zu versenden, link: https://control.txtlocal.co.uk
         */
        HttpResponse<String> response = GlobalApiRequest.requestAPI(link, data);

        if (response != null && response.statusCode() == 200){

            TelefonRegisterController telefonRegisterController = (TelefonRegisterController) viewService.switchTo(GlobalView.TELEFONREGISTER);
            telefonRegisterController.setLoginTelefon(zuRegisterSenden);

        } else {
            telefonLoginFehlerAusgabe("noSms", "no");
        }
    }

    /**
     * Telefon Field Validieren
     * 1. prüfen auf Zahl, ansonsten return null
     * 2. Length Prüfen
     * @param event
     */
    public void telefonValidate(KeyEvent event) {
        int maxTelLength = GlobalConfig.MAX_TEL_LENGTH;
        int minTelLength = GlobalConfig.MIN_TEL_LENGTH;

        /* 1. nur Zahlen erlaubt */
        telefonInput.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[0-9]*")){

                return change;
            }
            telefonLoginFehlerAusgabe("nurZahlen", "no");
            return null;
        }));

        /* 2. Max Length 10 Zahlen */
        telefonInput.textProperty().addListener((old, alt, neu) -> {
            if (telefonInput.getText().length() > maxTelLength){
                telefonInput.setText(alt);
            }
        });

        /* bei verlassen den Feld, prüfen auf Min. Zeichen */
        telefonInput.focusedProperty().addListener((observable, aBoll, t2) -> {
            if (!t2 && telefonInput.getText().length() < minTelLength){

                telefonInput.requestFocus();
                telefonLoginFehlerAusgabe("nummerFalsch", "no");
            }
        });
    }

    /**
     * Vorwahl Field Validieren
     * 1. prüfen auf Zahl, ansonsten return null
     * 2. Max Length auf 5 Zahlen (mit Plus)
     * 3. Focus verlassen = prüfen auf Plus Zeichen &
     *      Min Length 2 Zahlen
     *      z.b.s
     *      * Vorwahl nordamerika: +1 (001)
     *      * Vorwahl Montserrat: +1664
     * @param event
     */
    public void vorwahlValidate(KeyEvent event) {
        int vorwahlLength = GlobalConfig.VORWAHL_LENGTH;

        /* 1. nur Zahlen Erlaubt */
        vorwahlInput.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getText().matches("[+1-9]*")){
                return change;
            }
            telefonLoginFehlerAusgabe("nurZahlen", "no");
            return null;
        }));

        /* 2. Max. Length 5 Zahle (mit Plus) */
        vorwahlInput.textProperty().addListener((old, alt, neu) ->{
            if (vorwahlInput.getText().length() > vorwahlLength){
                vorwahlInput.setText(alt);
                telefonLoginFehlerAusgabe("vorwahlZuLang", "no");
            }
        });

        /* 3. onBlur: wenn das feld 'vorwahl' wird verlassen, geht in IF rein( t1 ist false) */
        vorwahlInput.focusedProperty().addListener((observableValue, aBoolean, t1) -> {

            /* Min. Length 2 Zahlen (MIT PLUS)  */
            if (!t1 && vorwahlInput.getText().length() < 2 ){

                vorwahlInput.setText("");
                vorwahlInput.requestFocus();

            }

            /* auf Pluszeichen prüfen */
            if (vorwahlInput.getText().indexOf("+") == -1) {

                vorwahlInput.setText("");
                vorwahlInput.requestFocus();
                telefonLoginFehlerAusgabe("plusZeichenFehlt", "no");

            }
        });

    }


    /**
     * zu Mail Login wechseln
     * @param event
     */
    public void mailLoginLinks(ActionEvent event) {
        viewService.switchTo(GlobalView.MAILLOGIN);
    }

    /**
     * Stage Fenster schliessen
     * @param event
     */
    public void telefonloginClose(ActionEvent event) {
        GlobalConfig.stageClose(event);
    }

    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    public void telefonloginDragged(MouseEvent event) {
        GlobalConfig.dragget(event);
    }
    public void telefonloginPressed(MouseEvent event) {
        GlobalConfig.pressed(event);
    }


    /**
     * Ausgabe von Allgemeine Fehler
     * @param text
     * @param ok
     */
    public void telefonLoginFehlerAusgabe(String text, String ok) {

        telefonLoginFehler.setTextFill(ok == "ok" ? Color.GREEN : Color.RED);
        switch (text){
            case "nurZahlen":           telefonLoginFehler.setText("erlaubt nur Zahlen");
                                        break;
            case "vorwahlZuLang":       telefonLoginFehler.setText("Falsche internationale Vorwahl(+1234)");
                                        break;
            case "plusZeichenFehlt":    telefonLoginFehler.setText("Das Pluszeichen fehlt (+)");
                                        break;
            case "nummerFalsch":        telefonLoginFehler.setText("Gib eine gültige Telefonnummer ein");
                                        break;
            case "noSms":               telefonLoginFehler.setText("MSM Nachricht wurde nicht gesendet");
                                        break;
            default:                    telefonLoginFehler.setText("Error: Telefon Login Cotroller Zeile: 220");
                                        break;
        }

        /* Fehler meldung nach 7 Sekunden ausblenden */
        PauseTransition pause = new PauseTransition(Duration.seconds(7));
        pause.setOnFinished(e -> telefonLoginFehler.setText(null));
        pause.play();
    }

}
