package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.configuration.GlobalView;
import BoteFx.configuration.GlobalViewSwitcher;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import org.slf4j.Logger;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private final Logger logger = GlobalConfig.getLogger(this.getClass());


    /**
     * zum maillogin.fxml wechseln
     * @param event
     */
    @FXML
    public void zumMailLogin(ActionEvent event) {
        GlobalViewSwitcher.switchTo(GlobalView.LOGINMAIL);
        logger.info("Home Controller(mail function)");
    }


    /**
     * zum telefonlogin.fxml wechseln
     * @param event
     */
    @FXML
    public void zumTelefonLogin(ActionEvent event) {
        GlobalViewSwitcher.switchTo(GlobalView.LOGINTELEFON);
        logger.info("Home Controller(telefon function)");
    }


    /**
     *  Stage schliessen
     * @param event
     */
    @FXML
    public void homeClose(ActionEvent event) {
        GlobalConfig.stageClose(event);
        logger.info("Home Close");
    }

    /**
     * Live Uhr
     * @param url
     * @param resourceBundle
     */
    @FXML private Label homeDatum;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /* 1. Uhr anzeigen: home.fxml */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                homeDatum.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            }
        };
        timer.start();
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
