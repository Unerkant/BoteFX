package BoteFx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 5.10.2022
 */

@Controller
public class TelefonController implements Initializable {

    @FXML private AnchorPane telefonAnchorPane;
    @FXML private ScrollPane telefonScroll;
    @FXML private VBox telefonVBox;
    @FXML private ColumnConstraints anrufDeleteAnzeigen;
    @FXML private ImageView telefonatID;
    @FXML private Label telefonPseudonym;
    @FXML private Label telefonDatum;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Telefonat Box auf 100% zehen
        AnchorPane.setTopAnchor(telefonAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(telefonAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(telefonAnchorPane, 0.0);
        AnchorPane.setRightAnchor(telefonAnchorPane, 0.0);
        telefonVBox.prefWidthProperty().bind(telefonScroll.widthProperty());
        System.out.println("Telefon Controller");
    }

    /**
     * Telefonat Löschen
     * @param event
     */
    public void telefonatDelete(MouseEvent event) {
        System.out.println("Telefonat Löschen Klick" + event);
    }

    public void anrufBearbeiten(MouseEvent event) {
        System.out.println("Telefonat Fenster Zeigen" + event);
    }
}
