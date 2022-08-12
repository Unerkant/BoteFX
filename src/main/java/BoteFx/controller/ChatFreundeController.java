package BoteFx.controller;

import BoteFx.configuration.GlobalConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;

public class ChatFreundeController {

    private final Logger logger = GlobalConfig.getLogger(this.getClass());

    @FXML private AnchorPane chatFreundeAnchorPane;
    @FXML private ImageView chatFreundeBild;
    @FXML private Label chatFreundeName;
    @FXML private ImageView chatFreundeHacken;
    @FXML private Label chatFreundeDatum;
    @FXML private TextArea chatFreundeText;
}
