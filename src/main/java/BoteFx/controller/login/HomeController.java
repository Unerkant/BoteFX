package BoteFx.controller.login;

import BoteFx.Enums.GlobalView;
import BoteFx.service.ConfigService;
import BoteFx.service.MethodenService;
import BoteFx.service.ViewService;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Controller
public class HomeController implements Initializable {

    @Autowired
    private ViewService viewService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private MethodenService methodenService;

    @FXML private Label homeDatum;
    @Value("${homeagbtext}")
    private String homeAgbText;
    @FXML private Label homeAgb;
    @Value("${homecopyright}")
    private String homeCopyrightText;
    @FXML private Label homeCopyright;


    /**
     * Live Uhr
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        /**
         *  1. Uhr anzeigen: home.fxml
         */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                homeDatum.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
        };
        timer.start();

        /* AGB Text anzeigen Quelle: application.properties  */
        homeAgb.setText(homeAgbText);

        /* Copyright anzeigen Quelle: application.properties */
        homeCopyright.setText(homeCopyrightText);

    }

    /**
     * zum maillogin.fxml wechseln
     * @param event
     */
    @FXML
    public void zumMailLogin(ActionEvent event) {
        viewService.switchTo(GlobalView.MAILLOGIN);
    }


    /**
     * zum telefonlogin.fxml wechseln
     * @param event
     */
    @FXML
    public void zumTelefonLogin(ActionEvent event) {
        viewService.switchTo(GlobalView.TELEFONLOGIN);
    }


    /**
     *  Stage schliessen
     * @param event
     */
    @FXML
    public void homeClose(ActionEvent event) {
        methodenService.stageClose(event);
    }


    /**
     * AUSGESETZT: SPÄTER LÖSCHEN
     * Stage Fenster an Bildschirm frei Positionieren
     * @param event
     * nicht vergessen im HomeController/AnchorPane function einzutragen
     * onMouseDragged="#homeDragged" onMousePressed="#homePressed"
     */
/*    @FXML
    public void homeDragged(MouseEvent event) {
        GlobalConfig.dragget(event);
        //logger.info("Home dragged");
    }*/
/*    @FXML
    public void homePressed(MouseEvent event) {
        GlobalConfig.pressed(event);
       //logger.info("Home pressed");
    }*/

}
