package BoteFx.controller;

import BoteFx.configuration.GlobalConfig;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class ChatFreundeController implements Initializable {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    @FXML private AnchorPane chatFreundeAnchorPane;
    @FXML private ImageView chatFreundeBild;
    @FXML private Label chatFreundePseudonym;
    @FXML private ImageView chatFreundeHacken;
    @FXML private Label chatFreundeDatum;
    @FXML private TextArea chatFreundeText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Chat Freunde Controller");
    } // Ende initialize

}
