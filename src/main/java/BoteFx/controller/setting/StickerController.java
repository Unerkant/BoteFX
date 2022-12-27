package BoteFx.controller.setting;

import BoteFx.controller.ChatBoxController;

import javafx.event.ActionEvent;
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
public class StickerController implements Initializable {

    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane stickerAnchorPane;
    @FXML private ScrollPane stickerScroll;
    @FXML private VBox stickerVBox;

    /**
     * Sticker Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(stickerAnchorPane, 0.0);
        AnchorPane.setRightAnchor(stickerAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(stickerAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(stickerAnchorPane, 0.0);
        stickerVBox.prefWidthProperty().bind(stickerScroll.widthProperty());
    }

    /**
     * Getter & Setter, Daten von SettingController zugesendet
     *
     * Daten:   1. stickertoken: meinen Token
     *          2. stickerhover: für den hover effect löschen
     */
    @FXML private String stickertoken;
    @FXML private GridPane stickerhover;

    public String getStickerToken() { return stickertoken; }
    public void setStickerToken(String stickerToken) { this.stickertoken = stickerToken; }

    public GridPane getStickerHover() { return stickerhover; }
    public void setStickerHover(GridPane stickerHover) { this.stickerhover = stickerHover; }


    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void stickerZuruck(ActionEvent event) {
        chatBoxController.changedPane("openfreunde");
        stickerhover.getStyleClass().remove("settingAktiv");
    }
}
