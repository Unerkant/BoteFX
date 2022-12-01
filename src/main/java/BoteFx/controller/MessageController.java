package BoteFx.controller;

import BoteFx.model.Message;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 24.11.2022
 */

@Controller
public class MessageController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    /**
     * Getter & Setter für Message Token
     * die messageChat() wird hier automatisch gestartet
     */
    private String messageToken;
    public String getMessageToken() {
        return messageToken;
    }
    public void setMessageToken(String mesagetoken) {
        this.messageToken = mesagetoken;
        messageChat(messageToken);
    }

    /**
     * Anzeige von Messages
     * @param chatToken
     */
    public void messageChat(String chatToken){
        //System.out.println("MessageController Zeile:37  " + messageToken);
    }


    /**
     * Foto Zeigen
     * @param mouseEvent
     */
    public void otherZeigen(MouseEvent mouseEvent) {
        System.out.println("Bild + Datei + Kamera");
    }


    public void messageWrap(KeyEvent keyEvent) {
        System.out.println("Message Wrap: " + keyEvent );
    }
}
