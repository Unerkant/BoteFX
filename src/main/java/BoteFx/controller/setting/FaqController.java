package BoteFx.controller.setting;

import BoteFx.controller.ChatBoxController;
import BoteFx.service.ConfigService;

import BoteFx.service.TranslateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 29.1ß.2022
 */

@Controller
public class FaqController implements Initializable {

    @Autowired
    private TranslateService translate;
 @Autowired
 ChatBoxController chatBoxController;

 @FXML private AnchorPane faqAnchorPane;
 @FXML private ScrollPane faqScroll;
 @FXML private VBox faqVBox;

 /**
  * FAQ Box auf 100% width ziehen
  *
  * @param url
  * @param resourceBundle
  */
 @Override
 public void initialize(URL url, ResourceBundle resourceBundle) {
   AnchorPane.setTopAnchor(faqAnchorPane, 0.0);
   AnchorPane.setRightAnchor(faqAnchorPane, 0.0);
   AnchorPane.setBottomAnchor(faqAnchorPane, 0.0);
   AnchorPane.setLeftAnchor(faqAnchorPane, 0.0);
   faqVBox.prefWidthProperty().bind(faqScroll.widthProperty());
 }

 /**
  * Setter & Getter, Daten zugesendet von SettingController
  * Daten:   1. faqtoken: meinen token
  *          2. faqhover: für den hover effect Löschen
  */
 @FXML private String faqtoken;
 @FXML private GridPane faqhover;
 public String getFaqToken() { return faqtoken; }
 public void setFaqToken(String faqToken) { this.faqtoken = faqToken; }

 public GridPane getFaqHover() { return faqhover; }
 public void setFaqHover(GridPane faqHover) { this.faqhover = faqHover; }


 /**
  * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
  *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
  *
  * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
  *    ChatBoxController/changedPane() Zeile: 356
  *
  *  3. der hover effect an die Positionen wird gelöscht
  */
 public void faqZuruck(ActionEvent event) {
    translate.closeStackPane();
    faqhover.getStyleClass().remove("settingAktiv");
 }

}
