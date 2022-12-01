package BoteFx.controller.setting;

import BoteFx.controller.ChatBoxController;

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
 * Den 23.10.2022
 */
@Controller
public class AllgemeinController implements Initializable {

    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane allgemeinAnchorPane;
    @FXML private ScrollPane allgemeinScroll;
    @FXML private VBox allgemeinVBox;

    /**
     * Allgemeine AnchrPane auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(allgemeinAnchorPane, 0.0);
        AnchorPane.setRightAnchor(allgemeinAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(allgemeinAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(allgemeinAnchorPane, 0.0);
        allgemeinVBox.prefWidthProperty().bind(allgemeinScroll.widthProperty());
    }


    /**
     * Setter & Getter, Daten zugesendet von SettingController
     */
    @FXML private String allgemeintoken;
    @FXML private GridPane allgemeinhover;

    public String getAllgemeinToken() { return allgemeintoken; }
    public void setAllgemeinToken(String allgemeinToken) { this.allgemeintoken = allgemeinToken; }

    public GridPane getAllgemeinHover() { return allgemeinhover; }
    public void setAllgemeinHover(GridPane allgemeinHover) { this.allgemeinhover = allgemeinHover; }



    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     * 3. der hover effect an die Positionen wird gelöscht
     */
    @FXML
    void allgemeinZuruck() {
        chatBoxController.changedPane("openfreunde");
        allgemeinhover.getStyleClass().remove("paneAktiv");
    }

}
