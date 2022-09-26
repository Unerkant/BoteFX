package BoteFx.controller.login;

import BoteFx.configuration.GlobalApiRequest;
import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

/**
    *   den 20.05.2022
    */
@Controller
public class MailLoginController implements Initializable {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;

    @FXML private VBox mailLoginHauptVBox;
    @FXML private AnchorPane mailLoginAnchorPane;
    @FXML private Label mailLoginFehler;
    @FXML private Label mailLoginInfo;
    @Value("${maillogininfo}")
    private String maillogininfo;
    @FXML private TextField mailLoginInput;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // maillogin.fxml -> AnchorPane auf 100% Height ziehen
        mailLoginAnchorPane.prefHeightProperty().bind(mailLoginHauptVBox.heightProperty());

        // Allgemeine information zu Registrierung oder Einloggen
        mailLoginInfo.setText(maillogininfo);

    }


    /**
     *  1. Validate: E-Mail Prüfen und versenden an Bote zu Mail-Sender,
     *  Request senden an, Bote -> apiMailController -> @PostMapping(value = "/mailApi")
     *  schließlich wird eine Activierungs-Code an diese E-Mail Vesendet
     *
     *  Request-response Status 200: weiter zum Code eingabe
     *  Request-response Status 404: Fehler ausgeben
     *
     */
    private String newUserMail;
    @FXML
    public void mailPrufen() {

        boolean valid;
        newUserMail = mailLoginInput.getText();
        valid = GlobalConfig.mailValid(newUserMail);

        if (valid){

            String apiUrl = GlobalConfig.FILE_HTTP+"mailApi";
            String json = "{ \"neuUserMail\":"+newUserMail+" }";

            /* Request Senden an Bote/ApiMailController.java-> @PostMapping(value = "/mailApi") */
            HttpResponse<String> response = GlobalApiRequest.requestAPI(apiUrl, json);

            if (response != null && response.statusCode() == 200) {

                MailRegisterController mailRegisterController = (MailRegisterController) viewService.switchTo(GlobalView.MAILREGISTER);
                mailRegisterController.setLoginEmail(mailLoginInput.getText());

            } else {
                mailLoginFehlerAusgabe("nichtversendet", "no");
            }

        }else{
            mailLoginFehlerAusgabe("falschemail", "no");
            return;
        }
    }



    /**
    *  1. Validate: E-Mail in Kleinbuchstaben konvertieren.
    */
    public void mailLowerCase(KeyEvent keyEvent) {
        mailLoginInput.textProperty().addListener((ov, oldValue, newValue )->{
            mailLoginInput.setText(newValue.toLowerCase());
            //logger.info(" toLowerCase ");
        });
    }


    /**
    *  2. Validate: die Maximale Länge einer E-Mail: 254
    *
    *  Daraus ergibt sich eine maximale Länge der E-Mail-Adresse
    *  von 254 Oktetten einschließlich des „@“. Eine längere Adresse kann über
    *  RFC-konforme SMTP-Server weder E-Mails versenden noch empfangen.
    */
    public void mailLength(KeyEvent keyEvent) {
        int maxLimit = GlobalConfig.MAIL_LENGTH;
        mailLoginInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obValue, String oldValue, String newValue) {
                if (mailLoginInput.getText().length() > maxLimit){
                    String s = mailLoginInput.getText().substring(0, maxLimit);
                    mailLoginInput.setText(s);

                    mailLoginFehlerAusgabe("mailzugross", "no");
                }
            }
        });
    }


    /**
     * Wechseln zu telefonlogin.fxml
     * @param event
     */
    @FXML
    public void telefonLoginLinks(ActionEvent event){
        viewService.switchTo(GlobalView.TELEFONLOGIN);
    }


    /**
     * mit der Maus Stage Fenster auf dem Bildschirm frei Bewegen
     * @param event
     */
    @FXML
    public void mailloginDragged(MouseEvent event) {
        GlobalConfig.dragget(event);
    }
    @FXML
    public void mailloginPressed(MouseEvent event) {
        GlobalConfig.pressed(event);
    }


    /**
     * Stage Fenster schliessen...
     * @param event
     */
    @FXML
    public void mailloginClose(ActionEvent event){

        GlobalConfig.stageClose(event);
    }


    /**
     * Fehler Ausgeben
     */
    public void mailLoginFehlerAusgabe(String text, String ok){

        mailLoginFehler.setTextFill(ok == "ok" ? Color.GREEN : Color.RED);

        switch (text){
            case "mailloginbutton": mailLoginFehler.setText("Fehler von Methode allSuchen()");
                                    break;
            case "mailzugross":     mailLoginFehler.setText("E-Mail ist zu Groß, erlaubt sind nur 50 Zeichen");
                                    break;
            case "gultigemail":     mailLoginFehler.setText(newUserMail);
                                    break;
            case "falschemail":     mailLoginFehler.setText("Keine Gültige E-Mail");
                                    break;
            case "nichtversendet":  mailLoginFehler.setText("Aktivierungscode wurde nicht versendet ...");
            default:                break;
        }
        //mailLoginFehler.setText(text);
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> mailLoginFehler.setText(null));
        pause.play();
    }
}
