package BoteFx.service;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.stereotype.Service;

/**
 * Den 6.3.2023
 */

@Service
public final class TranslateService implements ChangeListener<Number>, ListChangeListener<Node> {

    /**
     * Globale variable, nicht Löschen
     *
     * aktivePane:  bei verkleinerung die BoteApp sorgt das richtige Pane leftPane
     *              oder rightPane wird angezeigt.
     *              a. wenn in der Rechte Pane(rechtsPane) wird die message angezeigt
     *                dann bei Verkleinerung die BoteApp wird die RechtePane angezeigt
     *              b. wenn die Rechte Pane Leer ist, wird die linksPane angezeigt
     *              c. aktivePane = false;      linksPane anzeigen
     *              d. aktivePane = true;       rechtsPane anzeigen
     *
     *
     * aktuelleWidth: aktuelle width den hauptsPane(StackPane, haupt Pane von chatbox.fxml)
     */
    private int hauptWidth;
    private boolean aktivePane = false;
    private int aktuelleWidth;


    /**
     * Setter & Getter
     * zugesendet von ChatBoxController
     */
    private StackPane hauptsPane;
    public StackPane getHauptsPane() {
        return hauptsPane;
    }
    public void setHauptsPane(StackPane hauptspane) {
        this.hauptsPane = hauptspane;
    }

    private StackPane linksPane;
    public StackPane getLinksPane() {
        return linksPane;
    }
    public void setLinksPane(StackPane linkspane) {
        this.linksPane = linkspane;
    }

    private StackPane rechtsPane;
    public StackPane getRechtsPane() {
        return rechtsPane;
    }
    public void setRechtsPane(StackPane rechtspane) {
        this.rechtsPane = rechtspane;
    }


    /* ******************************** Auto Resize ************************************ */

    /**
     * Auto resize, (voll automatisch)
     * start in ChatBoxController/initialize/translate.layoutResize();
     *
     * ACHTUNG: bei verkleinerung die App unter 650px und wenn die Rechte Pane Leer ist,
     * wird dann die Linke Seite angezeigt, bei Anzeige in der rechte Pane wird dann
     * automatisch dir rechte Pane angezeigt....
     * siehe:  public void offenStackPane(){... ... aktivePane = true; }
     *
     * min. verkleinerung ist width/height 380/550px
     */
    @FXML
    public void layoutResize(){

        hauptsPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number altVal, Number newVal) {
                hauptWidth = (int) hauptsPane.getWidth();
                aktuelleWidth = newVal.intValue();
                if (aktuelleWidth < 651){
                    if (aktivePane){
                        // wenn rightStackPane mit message gefühlt ist
                        rightStackPaneGross();
                    } else {
                        // wenn rightPane Leer ist
                        leftPaneGross();
                    }
                } else {
                    // hauptStackPane über 650px
                    paneStandard();
                }
            }
        });
    }

    /**
     * Linke Pane voll anzeigen (max width: 650px)
     * funktioniere nur unter 650px width hauptPane
     */
    private void leftPaneGross(){
        linksPane.setPrefWidth(aktuelleWidth);
        linksPane.setMinWidth(aktuelleWidth);
        AnchorPane.setLeftAnchor(rechtsPane, Double.valueOf(aktuelleWidth));
    }

    /**
     * Rechte Pane voll anzeigen (max width: 650px)
     * funktioniere nur unter 650px width hauptPane
     */
    private void rightStackPaneGross(){
        linksPane.setPrefWidth(aktuelleWidth);
        linksPane.setMinWidth(aktuelleWidth);
        AnchorPane.setLeftAnchor(rechtsPane, 0.0);
    }

    /**
     * Linke Pane width: 300px
     * Rechte Pane 380px
     * funktioniere nur, wenn hauptPane über 650px width ist
     */
    private void paneStandard(){
        linksPane.setPrefWidth(300);
        linksPane.setMinWidth(300);
        AnchorPane.setLeftAnchor(rechtsPane, 300.0);
    }


    /* **************************** Translate *********************************** */

    /**
     * Slide funktion für die geladene fxml + rechte StackPane
     *
     * <code>
     *
     *     @Autowired
     *     private LayoutTranslate translate;
     *
     *     translate.offenStackPane();
     *     translate.closeStackPane();
     *     translate.slidePane(rechtesPane.getChildren());
     *     translate.schliessenPane(userinfopane);
     *
     * </code>
     *
     * BEISPIELE:
     *      Start haupt fxml
     *      @FXML
     *     public void telefonate(MouseEvent event) {
     *         // vor dem Laden, alle children Löschen außer tippPane(nur haupt fxml)
     *         translate.deleteAllPane(rightPane.getChildren());
     *         // wo soll geladen
     *         layoutService.setausgabeLayout(freundePane);
     *         TelefonController telefonController = (TelefonController) layoutService.switchLayout(GlobalView.TELEFON);
     *         // Slide function(nur unter 650px, ansonsten sofort)
     *         translate.getRechtsPane();
     *     }
     *     Schliessen haupt fxml
     *     @FXML
     *     public void messageZuruck() {
     *         //nach Schließung haupt fxml, alle children in der Rechte Pane löschen, außer tippPane
     *         translate.closeStackPane();
     *     }
     *
     *     Start fxml in 2 reihe...
     *      @FXML
     *     public void info() {
     *          //wo soll geladen, ACHTUNG: kein children Löschen vor dem Laden...
     *         layoutService.setausgabeLayout(rechtesPane);
     *         UserInfoController userInfoController = (UserInfoController) layoutService.switchLayout(GlobalView.USERINFO);
     *          // slide function, letzte geladene fxml(siehe changed...node)
     *         translate.slidePane(rechtesPane.getChildren());
     *     }
     *     Schliessen fxml von der 2 reihe
     *     @FXML
     *     public void infoZuruck() {
     *         // ID von geschlossen fxml mitsenden(@FXML private AnchorPane userinfopane;)
     *         translate.schliessenPane(userinfopane);
     *     }
     */


    /**
     * METHODE: kurze beschreibung
     *  1. slidePane + slidePane + changed:
     *      nur für die geladenen fxml-dateien, 2 Reihe was werden ins Rechte StackPane geladen über den
     *      haupt-Pane anzeige(message, setting, telefon....) 2 Reihe sind die infos oder Bearbeitung fxml
     *      start fxml 2 reihe: translate.slidePane(rechtesPane.getChildren());
     *      in der 'changed' pane, ist die letzte geladene fxml
     *
     *  2. onChanged:       nicht benutzt, keine verwendung,
     *                      vorgesehen: wenn neue fxml wird geladen dann alte wird automatisch gelöscht
     *  3. schliessenPane:  nur für die Schließung von fxml-dateien 2 Reihe, welche sind in Rechte
     *                      StackPane geladen...(nicht selbe StackPane), siehe Pos. 1
     *
     *  4. offenStackPane:  nur für die anzeige die Rechte StackPane und nur bei verkleinert
     *                      die BoteApp unter 650px width...
     *                      wenn width ist über 650px dann geladene fxml wird sofort angezeigt
     *                      über den FXMLLoader (layoutService.setausgabeLayout(rightPane);)
     *  5. closeStackPane:  nur für die Schließung von Rechte StackPane, nach dem schliessung wird
     *                      den inhalt der Rechte StackPane gelöscht, außer den tippPane (deleteAllPane)
     *
     *  6. deletePane:      alle fxml von 2 Reihe werden nach dem schliessung gelöscht
     *  7. deleteAllPane:   Alle haupt fxml von dem neuen Laden oder schliessung werden
     *                      aus der Rechte StachPane gelöscht, außer tippPane
     */


    /**
     * Start slide function + 2 untere Methoden
     * @param nodes
     */
    @FXML
    public void slidePane(ObservableList<Node> nodes){
        nodes.forEach(this::slidePane);
        nodes.addListener(this);
    }
    private void slidePane(Node node){
        node.layoutXProperty().addListener(this);
        node.layoutYProperty().addListener(this);
    }

    /**
     * Slide/offnen funktion nur für fxml 2 Reihe
     *
     * @param obs
     * @param altValue
     * @param newValue
     */
    @Override
    public void changed(ObservableValue<? extends Number> obs, Number altValue, Number newValue) {

        final DoubleProperty doubleProperty = (DoubleProperty) obs;
        // Letzte geladene Node
        final Node node = (Node) doubleProperty.getBean();
        // slide von rechts nach links
        node.translateXProperty().set(rechtsPane.getWidth());
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event -> {
            //System.out.println("Finisch Play Slider: ");
        });
        timeline.play();
    }

    /**
     * ACHTUNG: UNBENUTZT
     *
     * nicht Löschen: ist ein Teil von diesem class
     *
     * @param change
     */
    @Override
    public void onChanged(Change<? extends Node> change) {
      /*  while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::slider);
            } else if (change.wasRemoved()) {
                change.getRemoved().forEach(this::slider);
            }
        }*/
        //System.out.println("onChanged: " + change);
    }


    /**
     * Slide/schliessen funktion nur fxml 2 Reihe
     *
     * @param pane
     */
    public void schliessenPane(Pane pane){

        //Slide von links nach rechts
        pane.translateXProperty().set(0);
        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(pane.translateXProperty(), rechtsPane.getWidth(), Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(event -> {
            deletePane(pane);
            //System.out.println("Finisch Play Slider: ");
        });
        timeline.play();
    }


    /**
     * nur für Anzeigen die Rechte StackPane(rechtsPane)
     * selbst den fxml wird durch FXMLLoader ins rechtsPane geladen
     * funktioniere nur unter 650px hauptsPane
     *
     */
    public void offenStackPane(){

        if (hauptWidth < 651){
            Thread tr = new Thread() {
                @Override
                public void run() {
                    for (int z = aktuelleWidth; z >= 0; z--) {

                        try {
                            Thread.sleep(0, 500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        linksPane.setPrefWidth(aktuelleWidth);
                        linksPane.setMinWidth(aktuelleWidth);
                        AnchorPane.setLeftAnchor(rechtsPane, Double.valueOf(z));
                    }
                }
            }; tr.start();
        } else {
            // wenn hauptStackPane breiter als 650px ist, nichts machen
        }

        // merken, die RechtePane ist aktiv (in der RechtePane wird etwas ausgegeben)
        aktivePane = true;
    }


    /**
     * nur für schliessen die Rechte StackPane(rechtsPane)
     * funktioniere nur unter width 650px der hauptsPane
     */
    public void closeStackPane(){

        if (hauptWidth < 651){
            Thread tr = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i <= aktuelleWidth; i++) {

                        try {
                            Thread.sleep(0, 500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        /**
                         * leftPane.setPrefWidth(i);
                         * ACHTUNG: nicht prefWidth setzen, drückt die LinkePane zusammen(bis 300px)
                         */
                        linksPane.setMinWidth(i);
                        //AnchorPane.setLeftAnchor(rightStackPane, Double.valueOf(i));
                        AnchorPane.setLeftAnchor(rechtsPane, Double.valueOf(i));
                    }
                    Platform.runLater(() -> deleteAllPane(rechtsPane.getChildren()));
                }
            }; tr.start();
        } else {
            deleteAllPane(rechtsPane.getChildren());
        }
        // merken löschen
        aktivePane = false;
    }


    /**
     * Löscht genau die geschlossene Pane aus den 2 Reihe
     * ausgelöst bei schliessenPane... Zeile: 251
     * @param pane
     */
    private void deletePane(Pane pane){
        rechtsPane.getChildren().remove(pane);
        //System.out.println("Schliessen: " + rechtsPane.getChildren());
    }


    /**
     * alle children in den Rechte StackPane werden gelöscht, außer die tippPane..
     * wenn, fxml-datei werden neu geladen oder geschlossen
     * die children aus dem 2 Reihe werden hier nicht gelöscht...
     *
     * @param auserTippPane
     */
    public void deleteAllPane(ObservableList<Node> auserTippPane){
        auserTippPane.remove(1, auserTippPane.size());
        //System.out.println("Delete All, außer tipp Pane: " + rechtsPane.getChildren());
    }

}

