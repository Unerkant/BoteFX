package BoteFx.controller.setting;

import BoteFx.configuration.BoteConfig;
import BoteFx.service.SwitchButton;
import BoteFx.service.TranslateService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Den 29.10.2022
 */

@Controller
public class SmileController implements Initializable {


    @FXML private StackPane smileHauptStack;

    @FXML private GridPane anzeigeSwitchGrid;
    @FXML private GridPane wiederholenSwitchGrid;
    @FXML private GridPane sortierenSwitchGrid;
    @FXML private StackPane anzeigeSwitchStack;
    @FXML private StackPane wiederholenSwitchStack;
    @FXML private StackPane sortierenSwitchStack;
    public VBox stickerPaketeVBox;


    @Autowired
    private TranslateService translate;



    /**
     * Sticker Box auf 100% width ziehen
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Switz-Button erstellen & verwaltem
        createSwitchHandle();
        // Sticker Pakete Laden und in for Schleife ausgeben
        stickerPaketeHandle();
    }

    /**
     * Getter & Setter, Daten von SettingController zugesendet
     *
     * Daten:   1. stickertoken: meinen Token
     *          2. stickerhover: für den hover effect löschen
     */
    @FXML private String smileToken;
    @FXML private GridPane smileHover;

    public String getSmileToken() { return smileToken; }
    public void setSmileToken(String smileToken) { this.smileToken = smileToken; }

    public GridPane getSmileHover() { return smileHover; }
    public void setSmileHover(GridPane smileHover) { this.smileHover = smileHover; }



    /* ::::::::::::::::::::::::: Create Switch Buttons ::::::::::::::::::::::::::::::: */

    /**
     * Create Switch Buttons + anbinden ins vorgesehene StackPane
     */
    @Autowired
    private BoteConfig boteConfig;
    @Autowired
    private SwitchButton switchButton;
    private List<Pane> ausgabeSwitchStack   = new ArrayList<>();
    private List<Pane>  clickSwitchGrid     = new ArrayList<>();
    private List<String>    smilePropertiesKey   = new ArrayList<>();
    private void createSwitchHandle(){
        // Alle StackPane in einem array sammeln, welche für switch-button sind vorgesehen
        ausgabeSwitchStack.add(anzeigeSwitchStack);
        ausgabeSwitchStack.add(wiederholenSwitchStack);
        ausgabeSwitchStack.add(sortierenSwitchStack);

        // alle GridPane in einem array sammeln, von diese GridPane wird den swicht-button click ausgeführt
        clickSwitchGrid.add(anzeigeSwitchGrid);
        clickSwitchGrid.add(wiederholenSwitchGrid);
        clickSwitchGrid.add(sortierenSwitchGrid);

        // alle properties in einem Array sammeln, welche sind in boteconfig.properties eingetragen für die Smile
        smilePropertiesKey.add("smileAnzeigen");
        smilePropertiesKey.add("smileWiederholen");
        smilePropertiesKey.add("smileSortieren");

        // Switch-Button erstellen
        switchButton.createButton(ausgabeSwitchStack, clickSwitchGrid, smilePropertiesKey);
    }

    /**
     * Smile Anzeigen Einstellungen
     * @param event
     */
    public void anzeigeSwitchHandle(MouseEvent event) {

        if (switchButton.isSwitchedOn()) {
            booleanProperties("smileAnzeigen", "true");
        } else {
            booleanProperties("smileAnzeigen", "false");
        }
    }

    /**
     * Smile Wiederholen Einstellungen
     * @param event
     */
    public void wiederholenSwitchHandle(MouseEvent event) {
        if (switchButton.isSwitchedOn()) {
            booleanProperties("smileWiederholen", "true");
        } else {
            booleanProperties("smileWiederholen", "false");
        }
    }


    /**
     * Smile Sortieren Einstellungen
     * @param event
     */
    public void sortierenSwitchHandle(MouseEvent event) {
        if (switchButton.isSwitchedOn()) {
            booleanProperties("smileSortieren", "true");
        } else {
            booleanProperties("smileSortieren", "false");
        }
    }

    /**
     * in boteconfig.properties neuen wert setzen true/false
     * @param key
     * @param value
     */
    private void booleanProperties(String key, String value){
        boteConfig.setProperties(key, value);
    }


    /* :::::::::::::::::::::::::::::::::::: Weitere Methoden ::::::::::::::::::::::::: */

    public void angesagteSticker(MouseEvent event) {
    }

    public void archivierteSticker(MouseEvent event) {
    }


    /**
     * Sticker Pakete Ausgeben
     *
     * ACHTUNG: zuerst wird nur leeres Label ausgegeben, nur scroll zu Testen
     */
    private void stickerPaketeHandle(){

        for (int i = 0; i < 50; i++){
            Label lab = new Label();
            lab.setText(String.valueOf(i));
            stickerPaketeVBox.getChildren().add(lab);
        }
    }


    /* :::::::::::::::::::::::::::::: Zurück + Schliessen ::::::::::::::::::::::::::::::::::::: */

    /**
     * Smile Schliessen
     */
    public void smileSchliessen(MouseEvent event) {

        translate.hideHauptPane();
        smileHover.getStyleClass().remove("settingAktiv");
        //System.out.println("Smile Schliessen");
    }

}
