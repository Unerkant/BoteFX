package BoteFx.controller.login;

import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Controller
public class TelefonSuccessController implements Initializable {

    @Autowired
    private ViewService viewService;

    @FXML private VBox telSuccessVBox;
    @FXML private AnchorPane telSuccessArchorPane;
    @FXML private Label telSuccessUhr;
    @FXML private Label telSuccessName;
    @FXML private Label telSuccessVorname;
    @FXML private Label telSuccessPsuedonym;
    @FXML private Label telSuccessDatum;
    @FXML private Label telSuccessMail;
    @FXML private Label telSuccessTelefon;
    @FXML private Label telSuccessToken;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* ArchorPane auf 100% height */
        telSuccessArchorPane.prefHeightProperty().bind(telSuccessVBox.heightProperty());

        /**
         *  1. Uhr anzeigen: mailsuccess.fxml
         */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                telSuccessUhr.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
        };
        timer.start();
    }


    /**
     * Getter & Setter
     * ein json-object mit registriert/Einloggen Daten von
     * TelefonRegisterController -> kodeRequest() -> Zeile: 90  wurden zugesendet.
     *
     */
    private String userDaten;
    public String getUserDaten(){
        return userDaten;
    }
    public void setUserDaten(String jsonDaten){
        this.userDaten = jsonDaten;
        anzeigeDaten();
    }


    /**
     * Personliche Daten
     * Ausgabe alle Personliche Daten aus dem Datenbank
     * json-object von getUserDaten() holen & ausgeben
     */
    public void anzeigeDaten(){
        String gelesenJson = getUserDaten();
        JSONObject ob = new JSONObject(gelesenJson);


       /* obj(output) = {"otherOutput":"","bildOutput":"","datumOutput":"26-08-2022 19:08:42","nameOutput":"","pseudonymOutput":"CH",
       "tokenOutput":"26082022190842","telefonOutput":"","mailOutput":"chrome@chrome.de","roleOutput":"default","vornameOutput":""} */

        String responseOther       = ob.getString("otherResponse");
        String responseBild       = ob.getString("bildResponse");
        String responseDatum      = ob.getString("datumResponse");
        String responseName       = ob.getString("nameResponse");
        String responsePseudonym  = ob.getString("pseudonymResponse");
        String responseToken      = ob.getString("tokenResponse");
        String responseTelefon    = ob.getString("telefonResponse");
        String responseMail       = ob.getString("mailResponse");
        String responseRole       = ob.getString("roleResponse");
        String responseVorname    = ob.getString("vornameResponse");

        /* User Daten anzeigen */
        if (responseName.isBlank()) {
            telSuccessName.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessName.setText(responseName);
        }
        if (responseVorname.isBlank()) {
            telSuccessVorname.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessVorname.setText(responseVorname);
        }
        if (responsePseudonym.isBlank()) {
            telSuccessPsuedonym.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessPsuedonym.setText(responsePseudonym);
        }
        if (responseDatum.isBlank()) {
            telSuccessDatum.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessDatum.setText(responseDatum);
        }
        if (responseMail.isBlank()) {
            telSuccessMail.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessMail.setText(responseMail);
        }
        if (responseTelefon.isBlank()) {
            telSuccessTelefon.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessTelefon.setText("+"+responseTelefon);
        }
        if (responseToken.isBlank()) {
            telSuccessToken.setText(". . . . . . . . . . . . . . . . . . . . . . .");
        } else {
            telSuccessToken.setText(responseToken);
        }

    }


    /**
     * zu der Messenger
     * @param event
     */
    public void telSuccessZumMessenger(ActionEvent event) {
        viewService.switchTo(GlobalView.CHATBOX);
    }


    /**
     * zu der Messenger
     * @param event
     */
    public void telSuccessZumSetting(ActionEvent event) {
        viewService.switchTo(GlobalView.CHATBOX);
    }

}
