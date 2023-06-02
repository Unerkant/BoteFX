package BoteFx.controller.fragments;

import BoteFx.model.Message;
import BoteFx.service.ConfigService;

import BoteFx.service.TokenService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

    /**
      * Den 16.04.2023
      */
@Controller
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MessageCellController implements Initializable {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ConfigService configService;

    @FXML private StackPane mesageBox;
    @FXML private Label mesageUserBild;
    @FXML private Label mesageUserName;
    @FXML private Label mesageKorektur;
    @FXML private ImageView mesageGelesen;
    @FXML private ImageView mesageUngelesen;
    @FXML private Label mesageZeit;
    @FXML private Label mesageBilder;
    @FXML private TextFlow mesageTextFlow;
    @FXML private Text mesageTextPane;
    @FXML private AnchorPane mesageAnchorPaneCheckBox;
    @FXML private VBox mesageVBoxCheckBox;
    @FXML private CheckBox mesageCheckBox;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ....
    }



    /**
     * Pseudonym hintergrund Color zugesendet von MessageController/ Zeile: 220
     */
    private String friendColor;
    public String getFriendColor() { return friendColor; }
    public void setFriendColor(String friendColor) { this.friendColor = friendColor; }


    private String myColor;
    public String getMyColor() { return myColor; }
    public void setMyColor(String mycolor) { this.myColor = mycolor; }




    /**
     * Message Daten von MessageController zugesendet(schleife) Zeile: 200
     *
     * setProperty = sind Daten nur von einer einzelnen message, werde in MessageController
     * dur die schleife einzel zugesendet und ausgegeben/angezeigt
     */

    private Message mesages;
    public void setProperty(Message messages) {
        this.mesages = messages;

    // 1. Token holen
        final String freundToken = mesages.getFreundetoken();
        final String meinenToken = tokenService.meinToken();

    // 2. messageBox und checkBox eine ID zuweisen
        mesageBox.setId(String.valueOf(mesages.getId()));
        mesageCheckBox.setId(String.valueOf(mesages.getId()));
        mesageVBoxCheckBox.setId(String.valueOf(mesages.getId()));

    // 3. mesageUserBild.setText(cellMessage.getPseudonym());
        Image altImg = new Image(configService.FILE_HTTP+"profilbild/"+mesages.getMeintoken()+".png", false);
        if (altImg.isError()){
            mesageUserBild.setText(mesages.getPseudonym());
            mesageUserBild.setStyle( "-fx-background-color:" +
                    (meinenToken.equals(mesages.getMeintoken()) ? myColor : friendColor) + "; " +
                    "-fx-text-fill: white; -fx-font-family: \"Sans-Serif\"; -fx-font-weight: 700;");
        }else {
            ImageView altImageView = new ImageView(altImg);
            altImageView.setFitHeight(30);
            altImageView.setFitWidth(30);
            mesageUserBild.setGraphic(altImageView);
        }

    // 4.  Name oder Pseudonym
        mesageUserName.setText(mesages.getPseudonym());

    // 5. Message Datum Ausgeben
        mesageZeit.setText(mesages.getDatum().substring(8,14));

    // 6.
        mesageBilder.setText(mesages.getMeintoken());

    // 7. Text ausgeben
        mesageTextPane.setText(mesages.getText());

    }

}
