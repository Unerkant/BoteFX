package BoteFx.controller.fragments;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * den 12.03.2023
 */

@Controller
public class TippController implements Initializable {

    @FXML private BorderPane tippHauptPane;
    @FXML private VBox tippBox;
    @FXML public AnchorPane tippAusgabe;
    @FXML private ImageView tippInfo;
    @FXML private ImageView tippClose;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // noch keine verwendung
        Image happyImg = new Image(getClass().getResourceAsStream("/static/img/happy.png"), 100, 100, true, true);
        ImageView happy = new ImageView(happyImg);

        tippAusgabe.getChildren().add(happy);
    }

    /**
     * Tipp Fenster anzeigen/Ausblenden
     */
    @FXML
    public void tippAnzeigen() {

        if (tippBox.isVisible() == true){
            tippBox.setVisible(false);
            tippInfo.setVisible(true);
            tippClose.setVisible(false);
        } else {
            tippBox.setVisible(true);
            tippInfo.setVisible(false);
            tippClose.setVisible(true);
        }
    }

    public void tippBack(ActionEvent event) {
        System.out.println("Zurück");
    }

    public void tippNext(ActionEvent event) {
        System.out.println("Vor");
    }
}
