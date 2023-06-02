package BoteFx.controller.setting;

import BoteFx.service.TranslateService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 29.10.2022
 */

@Controller
public class SpracheController implements Initializable {

    @Autowired
    private TranslateService translate;

    @FXML private AnchorPane spacheAnchorPane;
    @FXML private ScrollPane spracheScroll;
    @FXML private VBox spracheVBox;

    /**
     * Sprache Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(spacheAnchorPane, 0.0);
        AnchorPane.setRightAnchor(spacheAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(spacheAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(spacheAnchorPane, 0.0);
        spracheVBox.prefWidthProperty().bind(spracheScroll.widthProperty());
    }


    /**
     * Setter & Getter, Daten von SettingController zugesendet
     * Daten:   1. profilToken: meinen token
     *          2. profilHover: hover effect Löschen
     */
    @FXML private String sprachetoken;
    @FXML private GridPane sprachehover;

    public String getSpracheToken() { return sprachetoken; }
    public void setSpracheToken(String spracheToken) { this.sprachetoken = spracheToken; }

    public GridPane getSpracheHover() { return sprachehover; }
    public void setSpracheHover(GridPane spracheHover) { this.sprachehover = spracheHover;}


    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void spracheZuruck() {
        translate.closeStackPane();
        sprachehover.getStyleClass().remove("settingAktiv");
    }
}
