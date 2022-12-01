package BoteFx.controller;

import BoteFx.service.ConfigService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 5.10.2022
 */

@Controller
public class KontakteController implements Initializable {

    @FXML private AnchorPane kontakteAnchorPane;
    @FXML private ScrollPane kontakteScroll;
    @FXML private VBox kontakteVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AnchorPane.setTopAnchor(kontakteAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(kontakteAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(kontakteAnchorPane, 0.0);
        AnchorPane.setRightAnchor(kontakteAnchorPane, 0.0);
        kontakteVBox.prefWidthProperty().bind(kontakteScroll.widthProperty());
        System.out.println("Kontakte Controller");
    }

    /**
     * Setter & Getter
     */

}
