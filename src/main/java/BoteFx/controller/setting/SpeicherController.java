package BoteFx.controller.setting;

import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 11.01.2024
 */

@Controller
public class SpeicherController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Speicher Controller");
    }

    /* :::::::::::::::::::::::::::::::: SETTER & GETTER ::::::::::::::::::::::::::::::::::::::::::: */
    private String speicherToken;
    private GridPane speicherHover;

    public String getSpeicherToken() { return speicherToken; }
    public void setSpeicherToken(String speicherToken) { this.speicherToken = speicherToken; }

    public GridPane getSpeicherHover() { return speicherHover; }
    public void setSpeicherHover(GridPane speicherHover) { this.speicherHover = speicherHover; }


}
