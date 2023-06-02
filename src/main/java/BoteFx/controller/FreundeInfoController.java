package BoteFx.controller;

import BoteFx.model.Freunde;
import BoteFx.model.Message;
import BoteFx.service.TranslateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * den 26.05.2023
 */

@Controller
public class FreundeInfoController implements Initializable {

    @Autowired
    private TranslateService translateService;


    @FXML private AnchorPane freundeInfoHauptPane;
    @FXML private StackPane freundeInfoHeaderStackPane;
    @FXML private Label freundeInfoName;
    @FXML private VBox freundeInfoAusgabeVBox;


    /**
     * Setter & Getter
     */
    @FXML private Freunde infoFreundData;
    public Freunde getInfoFreundData() { return infoFreundData; }
    public void setInfoFreundData(Freunde infofreunddata) {
        this.infoFreundData = infofreunddata;

        freundHeaderInfo(infoFreundData);
    }

    @FXML private ArrayList<Message> infoFreundMessage;
    public ArrayList<Message> getInfoFreundMessage() { return infoFreundMessage; }
    public void setInfoFreundMessage(ArrayList<Message> infoFreundMessage) {
        this.infoFreundMessage = infoFreundMessage;

        freundMessageInfo(infoFreundMessage);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //System.out.println("Freunde Info Init:");
    }


    private void freundHeaderInfo(Freunde freundData){
        freundeInfoName.setText(freundData.getFreundetoken());
    }

    private void freundMessageInfo(ArrayList<Message> infoFreundMessage) {

        for (Message infoMessage : infoFreundMessage){

            String str = infoMessage.getText();
            Text text = new Text(str);
            TextFlow textFlow = new TextFlow(text);

            freundeInfoAusgabeVBox.getChildren().add(textFlow);
        }

    }

    /**
     * Freunde Info Bearbeiten
     * @param event
     */
    public void freundeInfoBearbeiten(ActionEvent event) {
        System.out.println("Freunde Info Bearbeiten");
    }



    /**
     * Freunde Info Zurück, Fenster schliessen
     * @param event
     */
    public void freundeInfoZuruck(MouseEvent event) {

        translateService.schliessenPane(freundeInfoHauptPane);

    }

}
