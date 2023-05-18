package BoteFx.controller.fragments;

import BoteFx.controller.ChatBoxController;
import BoteFx.controller.MessageController;
import BoteFx.service.MethodenService;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 27.04.2023
 */

@Controller
public class WeiterleitenController implements Initializable {

    @Autowired
    private MessageController messageController;
    @Autowired
    private ChatBoxController chatBoxController;
    @Autowired
    private MethodenService methodenService;


    @FXML private VBox weiterleitenHauptPane;
    @FXML private StackPane weiterleitenStackPane;
    @FXML private ImageView weiterleitenSenden;
    @FXML private StackPane weiterleitenSmileStackPane;
    @FXML private ImageView weiterleitenSmileBlau;
    @FXML private ImageView weiterleitenSmile;
    @FXML private TextField weiterleitenTextField;
    @FXML private TextArea weiterleitenTextarea;
    @FXML private ScrollPane weiterleitenScrollPane;
    @FXML private VBox weiterleitenVBox;


    // StackPane von ChatBoxController
    @FXML private StackPane hauptsStages;

    /**
     * Getter & Setter
     */
    private String weiterleitenText;
    public String getWeiterleitenText() { return weiterleitenText;}
    public void setWeiterleitenText(String weiterleitentext) {
        this.weiterleitenText = weiterleitentext;

        messageWeiterleiten();
        weiterleitenSmileHandle();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        hauptsStages = chatBoxController.getHauptStackPane();

        // Textarea in Focus Setzen
        Platform.runLater(() -> weiterleitenTextarea.requestFocus());
    }



    private void messageWeiterleiten(){

        for (int i = 0; i < 500; i++){
            Label lab = new Label("Weiterleiten Controller: " + i);
            weiterleitenVBox.getChildren().add(lab);
        }
        //weiterleitenScrollPane.setVvalue(1.0);
    }


    public void weiterleitenTextareaReleased(KeyEvent keyEvent) {
        System.out.println("Textarea Tastendrück: " + keyEvent);
    }


    public void weiterleitenSenden() {

        messageController.messageBearbeitenStart();
        System.out.println("Weiterleiten Controller Senden: ");
    }


    /* ****************** Smile Teile/ hover effekt ****************** */

    private void weiterleitenSmileHandle(){

        popUpSmileShow();

    }

    @FXML AnchorPane smileHauptBox = new AnchorPane();
    private void popUpSmileShow(){

        weiterleitenSmileStackPane.hoverProperty().addListener((ObservableValue<? extends Boolean> obser, Boolean oldVal, Boolean show) ->{

            if (show){
                Label kopSmileLab = new Label("Smile Box");
                ScrollPane kopSmileScroll = new ScrollPane(kopSmileLab);
                HBox kopSmileHBox = new HBox(kopSmileScroll);
                kopSmileHBox.setStyle("-fx-border-color: #F2F2F2; -fx-border-width: 0 0 1 0;");
                HBox.setHgrow(kopSmileScroll, Priority.ALWAYS);

                Label allSmileLab = new Label("Alle Smile Anzeigen");
                VBox allSmileVBox = new VBox(allSmileLab);
                ScrollPane allSmileScroll = new ScrollPane(allSmileVBox);
                VBox.setVgrow(allSmileScroll, Priority.ALWAYS);

                VBox kastenSmileVBox = new VBox(kopSmileHBox, allSmileScroll);
                kastenSmileVBox.setStyle("-fx-background-color: green; -fx-min-height: 200; -fx-max-height: 300;");

                smileHauptBox.getChildren().add(kastenSmileVBox);
                smileHauptBox.setMouseTransparent(true);
                smileHauptBox.setStyle("-fx-background-color: transparent; ");
                AnchorPane.setLeftAnchor(kastenSmileVBox, 5.0);
                //AnchorPane.setTopAnchor(kastenSmileVBox, 0.0);
                AnchorPane.setRightAnchor(kastenSmileVBox, 5.0);
                AnchorPane.setBottomAnchor(kastenSmileVBox, 40.0);

                weiterleitenStackPane.getChildren().add(smileHauptBox);
                System.out.println("Smile Hover");
            } else {
                System.out.println("Smile Normal");
                methodenService.popUpFensterClose(smileHauptBox);
            }
        });
    }

    private void  popUpSmileHide(){
        System.out.println("Smile Hide");
    }


    /* ********************** Weiterleiten Close ***************** */

    /**
     * Message Weiterleiten Close
     */
    public void weiterleitenClose() {
        methodenService.popUpFensterClose(weiterleitenHauptPane);
    }


}
