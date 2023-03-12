package BoteFx.controller.setting;

import BoteFx.controller.ChatBoxController;
import BoteFx.service.ConfigService;
import BoteFx.service.TranslateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 28.10.2022
 */

@Controller
public class SicherheitController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    private ChatBoxController chatBoxController;

    @FXML private AnchorPane sicherheitAnchorPane;
    @FXML private ScrollPane sicherheitScroll;
    @FXML private VBox sicherheitVBox;

    /**
     * Sicherheit Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(sicherheitAnchorPane, 0.0);
        AnchorPane.setRightAnchor(sicherheitAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(sicherheitAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(sicherheitAnchorPane, 0.0);
        sicherheitVBox.prefWidthProperty().bind(sicherheitScroll.widthProperty());
    }


    /**
     * Setter & Getter, Daten sind von SettingController zugesendet
     *
     */
    @FXML private String sicherheittoken;
    @FXML private GridPane sicherheithover;

    public String getSicherheitToken() { return sicherheittoken;}
    public void setSicherheitToken(String sicherheitToken) { this.sicherheittoken = sicherheitToken; }

    public GridPane getSicherheitHover() { return sicherheithover; }
    public void setSicherheitHover(GridPane sicherheitHover) { this.sicherheithover = sicherheitHover; }



    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void sicherheitZuruck(ActionEvent event) {
        translate.closeStackPane();
        sicherheithover.getStyleClass().remove("settingAktiv");
    }


}
