package BoteFx.controller.fragments;

import BoteFx.controller.ChatBoxController;
import BoteFx.service.TranslateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 20.11.2022
 */

@Controller
public class MessageLeerController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    private ChatBoxController chatBoxController;

    @FXML private AnchorPane messageLeerAnchorPane;

    /**
     * AnchorPane auf 100% zihen
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(messageLeerAnchorPane, 0.0);
        AnchorPane.setRightAnchor(messageLeerAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(messageLeerAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(messageLeerAnchorPane, 0.0);
    }

    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     * 3. der hover effect an die Positionen wird gelöscht
     */
    public void messageLeerZuruck() {
        translate.hideHauptPane();
        System.out.println("Message Leer Zuruck");
        //((Pane) messageLeerAnchorPane.getParent()).getChildren().remove(messageLeerAnchorPane);
    }

    /**
     * Fehler Melden
     * Fehler entsteht, wenn message Token Leer ist
     * Quelle: FreundeCellController/chatLaden() Zeile: 166
     * if (messagesToken.isBlank()){ ... }
     */
    public void messageleerMelden() {
        System.out.println("MessageLeerController:");
    }
}