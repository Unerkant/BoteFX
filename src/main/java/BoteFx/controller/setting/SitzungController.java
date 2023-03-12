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
 * Den 29.10.2022
 */

@Controller
public class SitzungController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane sitzungAnchorPane;
    @FXML private ScrollPane sitzungScroll;
    @FXML private VBox sitzungVBox;

    /**
     * Sitzung Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(sitzungAnchorPane, 0.0);
        AnchorPane.setRightAnchor(sitzungAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(sitzungAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(sitzungAnchorPane, 0.0);
        sitzungVBox.prefWidthProperty().bind(sitzungScroll.widthProperty());
    }


    /**
     * Setter & Getter, Daten zugesendet von SettingController
     * Daten:   1. sitzungtoken: meinen token
     *          2. sitzunghover: für den hover effect Löschen
     */
    @FXML private String sitzungtoken;
    @FXML private GridPane sitzunghover;
    public String getSitzungToken() { return sitzungtoken; }
    public void setSitzungToken(String sitzungToken) { this.sitzungtoken = sitzungToken; }

    public GridPane getSitzungHover() { return sitzunghover; }
    public void setSitzungHover(GridPane sitzungHover) { this.sitzunghover = sitzungHover; }



    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void sitzungZuruck(ActionEvent event) {
        translate.closeStackPane();
        sitzunghover.getStyleClass().remove("settingAktiv");
    }
}
