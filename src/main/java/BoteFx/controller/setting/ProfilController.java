package BoteFx.controller.setting;

import BoteFx.controller.ChatBoxController;

import BoteFx.service.MethodenService;
import BoteFx.service.TokenService;
import BoteFx.service.TranslateService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Den 9.10.2022
 */

@Controller
public class ProfilController implements Initializable {

    @Autowired
    private MethodenService methodenService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TranslateService translate;
    @Autowired
    ChatBoxController chatBoxController;

    @FXML private AnchorPane profilAnchorPane;
    @FXML private ScrollPane profilScroll;
    @FXML private VBox profilVBox;

    /**
     * Profil Box auf 100% width Ziehen
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnchorPane.setTopAnchor(profilAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(profilAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(profilAnchorPane, 0.0);
        AnchorPane.setRightAnchor(profilAnchorPane, 0.0);
        profilVBox.prefWidthProperty().bind(profilScroll.widthProperty());
    }


    /**
     * Setter & Getter, Daten zugesendet von SettingController
     * Daten:   1. profiltoken: meinen token
     *          2. profilhover: für den hover effect Löschen
     */
    @FXML private String profiltoken;
    @FXML private GridPane profilhover;

    public String getProfilToken() { return profiltoken; }
    public void setProfilToken(String profilToken) { this.profiltoken = profilToken; }

    public GridPane getProfilHover() { return profilhover; }
    public void setProfilHover(GridPane hoverPane) { this.profilhover = hoverPane; }


    /**
     * 1. das Parameter "openfreunde" wird an die Methode changedPane(...) in
     *    ChatBoxController Zeile: 310 gesendet, funktioniert nur unter 650px width
     *
     * 2. Achtung: wenn Haupt-Fester ist großer als 650px wird die rightPane nur geleert.
     *    ChatBoxController/changedPane() Zeile: 356
     *
     *  3. der hover effect an die Positionen wird gelöscht
     */
    public void profilZuruck() {
        translate.closeStackPane();
        profilhover.getStyleClass().remove("settingAktiv");
    }

    /**
     * Personlichen Daten Ändern / Löschen & Speichern
     * @param event
     */
    public void profilSpeichern(ActionEvent event) {
        System.out.println("Profil Speichern: " + event);
    }


    /**
     * Abmelden von BoteFx
     *
     * wird nur in Datenbank(H2 Local) den User Token gelöscht und Stage geschlossen
     *
     * ACHTUNG: könnte überarbeitet auf: stage neu Starten statt schliessen und
     * einem Pop-up-Fenster für warnung ausgeben
     *
     * @param event
     */
    public void profilAbmelden(ActionEvent event) {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Möchten Sie sich wirklich abmelden?", ButtonType.YES, ButtonType.NO);
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        if (ButtonType.YES.equals(result)) {
            // no choice or no clicked -> don't close
            boolean geloscht = tokenService.deleteToken(profiltoken);
            methodenService.stageClose(event);
            System.out.println("Abmelden: " +geloscht);
        }


    }
}
