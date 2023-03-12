package BoteFx.controller.setting;


import BoteFx.controller.ChatBoxController;
import BoteFx.service.ConfigService;
import BoteFx.service.TranslateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
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
public class DarstellungController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane darstellungAnchorPane;
    @FXML private ScrollPane darstellungScroll;
    @FXML private VBox darstellungVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(darstellungAnchorPane, 0.0);
        AnchorPane.setRightAnchor(darstellungAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(darstellungAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(darstellungAnchorPane, 0.0);
        darstellungVBox.prefWidthProperty().bind(darstellungScroll.widthProperty());
    }

    /**
     * Setter & Getter für zugesendeten Daten
     */
    @FXML private String darstellungtoken;
    @FXML private GridPane darstellunghover;

    public String getDarstellungToken() { return darstellungtoken; }
    public void setDarstellungToken(String darstellungToken) { this.darstellungtoken = darstellungToken; }

    public GridPane getDarstellungHover() { return darstellunghover; }
    public void setDarstellungHover(GridPane darstellungHover) { this.darstellunghover = darstellungHover; }


    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void darstellungZuruck(ActionEvent event) {
        translate.closeStackPane();
        darstellunghover.getStyleClass().remove("settingAktiv");
    }

    public void erscheinungsbildMenu(MouseEvent event) {
        System.out.println("drei Punkte-Manü: " + event);
    }

}
