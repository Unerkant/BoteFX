package BoteFx.controller.setting;

import BoteFx.configuration.BoteConfig;
import BoteFx.service.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

/**
 * Den 29.10.2022
 */

@Controller
public class SpracheController implements Initializable {

    @Autowired
    private TranslateService translate;
    @Autowired
    private BoteConfig boteConfig;
    @Autowired
    private SwitchButton switchButton;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private MethodenService methodenService;


    @FXML private StackPane spracheHauptStack;
    @FXML private VBox spracheVBox;
    @FXML private Label leanderNichtUbersetzenAnzeigen;
    @FXML private GridPane switchClickGrid;
    @FXML private StackPane spracheSwitchStack;

    /**
     * Setter & Getter, Daten von SettingController zugesendet
     * Daten:   1. profilToken: meinen token
     *          2. profilHover: hover effect Löschen
     */
    @FXML private String sprachetoken;
    @FXML private GridPane sprachehover;

    public String getSpracheToken() { return sprachetoken; }
    public void setSpracheToken(String spracheToken) { this.sprachetoken = spracheToken; }

    public GridPane getSpracheHover() { return sprachehover; }
    public void setSpracheHover(GridPane spracheHover) { this.sprachehover = spracheHover;}


    /**
     * Globale variable
     *
     * ACHTUNG: muss späten die variable language im Init von der Datenbank: 'Entry' wert
     *          abrufen und in variable speichern, hier unten im init Zeile: 90
     */
    private String language;

    /**
     * Init für automatische Methode Start
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // My Token: 18062023211207
        //language = Locale.getDefault();
        /**
         * ACHTUNG: die variable muss mit anderen daten, aus dem Datenbank 'Entry' überschrieben werden
         * z.b.s entryService.loadSetting(userId), sprache holen, wie das geschehen wird noch
         * nicht geregelt, wahrscheinlich muss bei regiestrierung schon sprache ermitteln und ins
         * datenbank 'Entry' mit UserId speichern... dann abrufen und in variable 'language' speichern
         */
        language = "German";
        leanderNichtUbersetzenAnzeigen.setText(language);
        //listLand.add(language);

        //System.out.println("Sparache: " + languageService.getLastUsedOrDefaultLanguage());

        // Create Switch-Button für
        createSwitchHandle();
    }



    // 1 Position
    /* :::::::::::::::::::::::::::::: nicht Übersetzer Sprache wellen ::::::::::::::::::::::::::::: */


    /**
     * Auswählen von welche Sprache soll nicht Übersetz werden
     *
     * Kurze Beschreibung: methode: nichtUbersetzenHandle(){...}
     *  1. erste Teil vor dem schleife:
     *      zuerst wird einen VBox in eine scrollPane erstellt, wo wird später alles ausgegeben
     *      dann wird noch ein VBox angelegt, das ist das Haupt pop-up-Fenster da werden die scrollPane
     *      mit dem ausgabeVBox integriert
     *      am schluß wird eine AnchorPane mit transparente background erstellt, sie dehnt sich auf ganze
     *      rechte Seite, an die AnchorPane wird einen setOnMouseClick angelegt, den pop-up-fenster zu schlißen
     *      in der AnchorPane wird der Haupt-VBox zentriert ausgegeben, siehe code unten
     *
     *  2. Teil mit dem schleife:
     *
     * @param event
     */
    public void nichtUbersetzenHandle(MouseEvent event) throws IOException {


       // Ausgabe VBox mit scrollPane erstellen(wird in Haupt-pop-up-VBox integriert)
        VBox ausgabeVBox = new VBox();
        ausgabeVBox.setStyle("-fx-background-color: white;");
        ScrollPane scrollPane = new ScrollPane(ausgabeVBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // eine AnchorPane(voll breite & höche) + VBox(pop-up-fester) erstellen
        VBox hauptPopUpVBox = new VBox(scrollPane);
        hauptPopUpVBox.getStyleClass().add("messagesBearbeiten");
        hauptPopUpVBox.setStyle("-fx-max-height: 400;");
        AnchorPane spiegelungSpracheAnchor = new AnchorPane(hauptPopUpVBox);
        spiegelungSpracheAnchor.setStyle("-fx-background-color: transparent;");

        // VBox Zentrieren in AnchorPane
        hauptPopUpVBox.translateXProperty()
                .bind(spiegelungSpracheAnchor.widthProperty()
                .subtract(hauptPopUpVBox.widthProperty())
                .divide(2));
        // VBox von oben 140px
        AnchorPane.setTopAnchor(hauptPopUpVBox, 140.0);

        // AnchorPane mit dem VBox ins StackPane integrieren
        spracheHauptStack.getChildren().add(spiegelungSpracheAnchor);

        // Spiegelung Pop-up-Fenster schliessen
        spiegelungSpracheAnchor.setOnMouseClicked(e -> {
            //System.out.println("Click Anchor Pane: " + spiegelungSpracheAnchor);
            methodenService.popUpFensterClose(spiegelungSpracheAnchor);
        });

        /* ********** mit dem schleife in VBox(pop-up-fenster) alle Leander ausgeben ************** */

        // Bild(hacken) + Text(leander) aus der Datei holen
        ImageView hackenImg = new ImageView(new Image("/static/img/icons/hacken36.png", 12, 12, true, true));
        String file = "src/main/resources/static/json/laender.json";
        JsonArray leander = (JsonArray) new JsonParser().parse(new FileReader(file));

        // 2 separate VBox, oberste mit Hacken & untere ohne Hacken
        VBox boxMitHacken = new VBox();
        VBox boxOhneHacken = new VBox();

        // Alle Länder Ausgaben, getAsString() = ausgabe ohne doppelt zeichen
        for (int i = 0; i < leander.size(); i++){

            // GridPane für die Leander ausgabe
            GridPane leanderAusgabeGrid = new GridPane();
            leanderAusgabeGrid.getStyleClass().add("hypersLink");
            leanderAusgabeGrid.getColumnConstraints()
                       .addAll(new ColumnConstraints(20), new ColumnConstraints(Region.USE_COMPUTED_SIZE));

            if (leander.get(i).getAsString().equals(language)){
                leanderAusgabeGrid.setId(leander.get(i).getAsString());
            }


            if (leanderAusgabeGrid.getId() != null && leanderAusgabeGrid.getId().equals(leander.get(i).getAsString())){
                Label textMitHacken = new Label(leander.get(i).getAsString());
                leanderAusgabeGrid.add(hackenImg, 0, 0);
                leanderAusgabeGrid.add(textMitHacken, 1, 0);
                boxMitHacken.getChildren().add(leanderAusgabeGrid);
            } else {
                Label textOhneHacken = new Label(leander.get(i).getAsString());
                leanderAusgabeGrid.add(textOhneHacken, 1, 0);
                boxOhneHacken.getChildren().add(leanderAusgabeGrid);
            }



            leanderAusgabeGrid.setAccessibleText(leander.get(i).getAsString());

            int finalI = i;
            leanderAusgabeGrid.setOnMouseClicked(e -> {

                leanderAusgabeGrid.setId(leander.get(finalI).getAsString());
                //System.out.println("Click Leander: " + leander.get(finalI).getAsString());
            });

        }

        // 2 VBox ins Haupt-VBox(pop-up-fenster) integrieren
        ausgabeVBox.getChildren().addAll(boxMitHacken, boxOhneHacken);
    }



    // 2 Position
    /* ::::::::::::::::: Create Switch-Button + properties boolean setzen ::::::::::::::::::::::::: */

    private ArrayList<Pane> spracheStackList               = new ArrayList<>();
    private ArrayList<Pane> spracheGridList                = new ArrayList<>();
    private ArrayList<String> sprachePropertiesKey  = new ArrayList<>();

    private void createSwitchHandle(){
        // alle StackPane Array List für den Switch-Button
        spracheStackList.add(spracheSwitchStack);

        // alle GridPane, click auf den switch-Button, ausgeweitet auf ganze GridPane
        spracheGridList.add(switchClickGrid);

        // alle Properties Key, welche sind in boteconfig.properties angelegt unter die Sprache
        sprachePropertiesKey.add("spracheUbersetzen");

        switchButton.createButton(spracheStackList, spracheGridList, sprachePropertiesKey);

    }

    /**
     * Alle Chats Übersetzen
     * @param event
     */
    public void clickUbersetzenAlleChats(MouseEvent event) {
        if (switchButton.isSwitchedOn()){
            spracheBooleanSetzen("spracheUbersetzen", "true");
        } else {
            spracheBooleanSetzen("spracheUbersetzen", "false");
        }
    }

    /**
     * boolean wert in boteconfig.properties setzen/speichern an Sprache Übersetzen
     * @param key
     * @param value
     */
    private void spracheBooleanSetzen(String key, String value){
        boteConfig.setProperties(key, value);
    }



    /* ::::::::::::::::::::::::::::::::: Verschieden Methoden ::::::::::::::::::::::::::::::::::::: */

    /**
     *  1. Schließt die RechtePane, funktioniere nur unter 650px ansonsten schließt sofort
     *      siehe TranslateService, Zeile: 400
     *
     *  2. der hover effect an die Positionen wird gelöscht
     */
    public void spracheZuruck() {
        translate.hideHauptPane();
        sprachehover.getStyleClass().remove("settingAktiv");
    }


}
