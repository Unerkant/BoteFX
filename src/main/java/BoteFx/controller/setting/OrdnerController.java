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
public class OrdnerController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Ordner Controller");
    }

    /* :::::::::::::::::::::::::::::::::: SETTER & GETTER ::::::::::::::::::::::::::::::::::::::::: */
    private String ordnerToken;
    private GridPane ordnerHover;

    public String getOrdnerToken() { return ordnerToken; }
    public void setOrdnerToken(String ordnerToken) { this.ordnerToken = ordnerToken; }

    public GridPane getOrdnerHover() { return ordnerHover; }
    public void setOrdnerHover(GridPane ordnerHover) { this.ordnerHover = ordnerHover; }
}
