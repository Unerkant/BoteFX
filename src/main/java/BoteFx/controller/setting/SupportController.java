package BoteFx.controller.setting;

import BoteFx.controller.ChatBoxController;

import BoteFx.service.TranslateService;
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
public class SupportController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane supportAnchorPane;
    @FXML private ScrollPane supportScroll;
    @FXML private VBox supportVBox;

    /**
     * Support Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(supportAnchorPane, 0.0);
        AnchorPane.setRightAnchor(supportAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(supportAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(supportAnchorPane, 0.0);
        supportVBox.prefWidthProperty().bind(supportScroll.widthProperty());
    }

    /**
     * Setter & Getter, Daten zugesendet von SettingController
     * Daten:   1. supporttoken: meinen token
     *          2. supporthover: für den hover effect Löschen
     */
    @FXML private String supporttoken;
    @FXML private GridPane supporthover;
    public String getSupportToken() { return supporttoken; }
    public void setSupportToken(String supportToken) { this.supporttoken = supporttoken; }

    public GridPane getSupportHover() { return supporthover; }
    public void setSupportHover(GridPane supportHover) { this.supporthover = supportHover; }


    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void supportZuruck(ActionEvent event) {
        translate.closeStackPane();
        supporthover.getStyleClass().remove("settingAktiv");
    }
}
