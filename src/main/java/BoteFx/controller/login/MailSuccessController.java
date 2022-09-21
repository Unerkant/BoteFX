package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Controller
public class MailSuccessController implements Initializable {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());


    @Autowired
    private ViewService viewService;

    @FXML private VBox successVBox;
    @FXML private AnchorPane successArchorPane;
    @FXML private Label successUhr;
    @FXML private Label successName;
    @FXML private Label successVorname;
    @FXML private Label successPsuedonym;
    @FXML private Label successDatum;
    @FXML private Label successMail;
    @FXML private Label successTelefon;
    @FXML private Label successToken;
    @FXML private Hyperlink successInfo;

   /* registerDatum.substring(0,10).replace('-','.') */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /* ArchorPane auf 100% height */
        successArchorPane.prefHeightProperty().bind(successVBox.heightProperty());

        /**
         *  1. Uhr anzeigen: mailsuccess.fxml
         */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                successUhr.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
        };
        timer.start();
    }

    /**
     * Getter & Setter
     * ein json-object mit registriert/Einloggen Daten von
     * MailRegisterController -> codePrufen() -> Zeile: 134  wurden zugesendet.
     *
     */
    private String userDaten;
    public String getUserDaten(){
        return userDaten;
    }
    public void setUserDaten(String jsonDaten){
        this.userDaten = jsonDaten;
        datenAnzeigen();
    }


    /**
     * Personliche Daten
     * Ausgabe alle Personliche Daten aus dem Datenbank
     * json-object von getUserDaten() holen & ausgeben
     */
    public void datenAnzeigen(){
        String gelesen = getUserDaten();
        JSONObject obj = new JSONObject(gelesen);


       /* obj(output) = {"otherOutput":"","bildOutput":"","datumOutput":"26-08-2022 19:08:42","nameOutput":"","pseudonymOutput":"CH",
       "tokenOutput":"26082022190842","telefonOutput":"","mailOutput":"chrome@chrome.de","roleOutput":"default","vornameOutput":""} */

        String outputOter       = obj.getString("otherOutput");
        String outputBild       = obj.getString("bildOutput");
        String outputDatum      = obj.getString("datumOutput");
        String outputName       = obj.getString("nameOutput");
        String outputPseudonym  = obj.getString("pseudonymOutput");
        String outputToken      = obj.getString("tokenOutput");
        String outputTelefon    = obj.getString("telefonOutput");
        String outputMail       = obj.getString("mailOutput");
        String outputRole       = obj.getString("roleOutput");
        String outputVorname    = obj.getString("vornameOutput");

        /* User Daten anzeigen */
        if (outputName.isBlank()) {
            successName.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successName.setText(outputName);
        }
        if (outputVorname.isBlank()) {
            successVorname.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successVorname.setText(outputVorname);
        }
        if (outputPseudonym.isBlank()) {
            successPsuedonym.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successPsuedonym.setText(outputPseudonym);
        }
        if (outputDatum.isBlank()) {
            successDatum.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successDatum.setText(outputDatum);
        }
        if (outputMail.isBlank()) {
            successMail.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successMail.setText(outputMail);
        }
        if (outputTelefon.isBlank()) {
            successTelefon.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successTelefon.setText(outputTelefon);
        }
        if (outputToken.isBlank()) {
            successToken.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            successToken.setText(outputToken);
        }

    }

    /**
     * Zum Messenger
     *
     * @param event
     */
    public void successZumMessenger(ActionEvent event) {
        viewService.switchTo(GlobalView.CHATBOX);
    }

    /**
     * Zum Setting
     * @param event
     */
    public void successZumSetting(ActionEvent event) {
        viewService.switchTo(GlobalView.CHATBOX);
    }
}
