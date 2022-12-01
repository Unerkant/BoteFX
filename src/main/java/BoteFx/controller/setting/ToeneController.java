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
public class ToeneController implements Initializable {

    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane toeneAnchorPane;
    @FXML private ScrollPane toeneScroll;
    @FXML private VBox toeneVBox;

    /**
     * Töne Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(toeneAnchorPane, 0.0);
        AnchorPane.setRightAnchor(toeneAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(toeneAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(toeneAnchorPane, 0.0);
        toeneVBox.prefWidthProperty().bind(toeneScroll.widthProperty());
    }


    /**
     * Setter & Getter, Daten von SettingControlleer zugesendet
     * Daten:   1. profilToken: meinen token
     *          2. profilHover: für hover effect Löschen
     */
    @FXML private String toenetoken;
    @FXML private GridPane toenehover;

    public String getToeneToken() { return toenetoken; }
    public void setToeneToken(String toeneToken) { this.toenetoken = toeneToken; }

    public GridPane getToeneHover() { return toenehover; }
    public void setToeneHover(GridPane toeneHover) { this.toenehover = toeneHover; }



    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    @FXML
    public void toeneZuruck(ActionEvent event) {
        //chatBoxController.changedPane("openfreunde");
        toenehover.getStyleClass().remove("paneAktiv");
    }
}
