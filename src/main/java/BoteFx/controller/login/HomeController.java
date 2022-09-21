package BoteFx.controller.login;

import BoteFx.configuration.GlobalConfig;
import BoteFx.Enums.GlobalView;
import BoteFx.service.ViewService;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Scanner;

@Controller
public class HomeController implements Initializable {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    @Autowired
    private ViewService viewService;

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

    @SneakyThrows
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
        viewService.switchTo(GlobalView.LOGINMAIL);
        //logger.info("Home Controller(von Home stage zum Mail Login wechseln)");
    }


    /**
     * zum telefonlogin.fxml wechseln
     * @param event
     */
    @FXML
    public void zumTelefonLogin(ActionEvent event) {
        viewService.switchTo(GlobalView.LOGINTELEFON);
        //logger.info("Home Controller(von Home stage zum Telefon Login wechseln)");
    }


    /**
     *  Stage schliessen
     * @param event
     */
    @FXML
    public void homeClose(ActionEvent event) {
        GlobalConfig.stageClose(event);
        //logger.info("Home Close");
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
