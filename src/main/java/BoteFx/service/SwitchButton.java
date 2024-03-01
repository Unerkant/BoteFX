package BoteFx.service;

import BoteFx.configuration.BoteConfig;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Den 8.2.2024
 * @author Bote
 */
@Service
public class SwitchButton {


    @Autowired
    private BoteConfig boteConfig;

    /**
     * Getter & Setter
     *
     *  @return true if switch is ON, false - OFF
     *
     *  <code>
     *     @Autowired
     *     private BoteConfig boteConfig;
     *
     *      public void switchHandle() {
     *         if (switchButton.isSwitchedOn()) {
     *             boteConfig.setProperties("smileAnzeigen", "true");
     *         } else {
     *             boteConfig.setProperties("smileAnzeigen", "false");
     *         }
     *     }
     *  </code>
     */
    private boolean isSwitchedOn;
    public boolean isSwitchedOn() { return isSwitchedOn; }
    public void setSwitchedOn(boolean switchedOn) { isSwitchedOn = switchedOn; }


    /**
     * Translate Eingeschaften
     */
    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);


    /**
     *  Color: 0xd3d3d3ff(LightGray), 0x4169e1ff(RoyalBlue, #4169E1)
     */
    private Color colorOn = Color.ROYALBLUE;


    /**
     *  KURZE BESCHREIBUNG
     *
     * zur erstellen eine oder mehreren switch-buttons brauchen wir nur 3 Parameter
     *  1. List Array von StackPane, wo werden die switch-buttons ausgegeben
     *  2. List Array von GridPane, der click auf den switch-button wird auf ganze GridPane ausgeweitet
     *  3. List Array von properties Key, gespeicherte Key(smile=true) in boteconfig.properties
     *
     *  die 3 List-Array werden in Controller zusammen gesetzt und hier an createButton zugesendet,
     *  in der Methode, createButton wird den button zusmmen gesetzt in eine Pane danach in eine
     *  zugesendete StachPane per getChildren integriert.
     *
     *  bie eine click(translateHandle) auf die GridPane, zuerst wird ermittelt ob der switch-button
     *  aktiv ist, mit den background prüfung, wenn Blau ist dann aktiv, wenn Grau dann aus
     *  weiter wird an die methode circleTranslate eine boolean wert gesendet, der switch-button
     *  wird eingeschaltet oder ausgeschalte...
     *  bei Abschließung die animation werden wir noch die setter&getter variable isSwitchedOn
     *  false oder true setzen zum weiteren abruff von den controller, kurze Beschreibung Zeile: 30
     *
     * @param switchButtonStack
     * @param switchButtonGrid
     * @param propertiesKeys
     */
    public void createButton(List<Pane> switchButtonStack, List<Pane> switchButtonGrid, List<String> propertiesKeys){

        for (int i = 0; i < switchButtonStack.size(); switchButtonGrid.size(), propertiesKeys.size(), i++) {
            Rectangle rectangle = new Rectangle(34, 20);
            rectangle.setArcWidth(20);
            rectangle.setArcHeight(20);
            rectangle.setFill(Color.LIGHTGRAY);
            rectangle.setStroke(Color.LIGHTGRAY);
            rectangle.setId("rect" + i);

            Circle circle = new Circle(9);
            circle.setCenterX(10);
            circle.setCenterY(10);
            circle.setFill(Color.WHITE);
            circle.setStroke(Color.LIGHTGRAY);
            circle.setId("circle" + i);

            // Switch Button ins Pane integrieren + ausgabe in StackPane
            Pane pane = new Pane(rectangle, circle);
            pane.setMaxSize(34, 20);
            switchButtonStack.get(i).getChildren().add(pane);

            // Click aud die ganze GridPane, den switch-button ON oder OFF schalten
            switchButtonGrid.get(i).setOnMousePressed(event -> {
                translateHandle(rectangle, circle);
            });

            // switch-button ON schalten, wenn in boteconfig.properties auf true gesetzt ist
            String key = boteConfig.getProperties(propertiesKeys.get(i));
            if (key != null && key.equals("true")){

                // switch-button aktiv setzen
                rectangle.setStroke(colorOn);
                rectangle.setFill(colorOn);
                circle.translateXProperty().set(34 - 20);
            }
        }
    }


    /**
     * Prüfen, ob Switch-Button ON oder OFF ist,
     * hier wird angewendet ganz simple Methode, wenn background von rectangle blau ist, button ist aktiv(ON)
     * die variable isSwitchedOn wird false gesetzt und an die Methode circleTranslate() gesendet
     *
     * @param rect
     * @param circl
     */
    private void  translateHandle(Rectangle rect, Circle circl){

        if (rect.getFill().toString().equals(colorOn.toString())){
            isSwitchedOn = false;
            circleTranslate(isSwitchedOn, rect, circl);
        } else {
            isSwitchedOn = true;
            circleTranslate(isSwitchedOn, rect, circl);
        }

    }


    /**
     * translate animation:
     *          switch-button ON: background + border = color blau
     *          switch-button OFF: background + border = color grau
     *
     *  ACHTUNG: die variable(setter & getter) isSwitchedOn wird true oder false gesetzt für
     *  den weiteren ab-ruff von den controller...
     *
     * @param isOn
     * @param rectangle
     * @param circle
     */
    private void circleTranslate(boolean isOn, Rectangle rectangle, Circle circle){

        fillAnimation.setShape(rectangle);
        translateAnimation.setNode(circle);
        translateAnimation.setToX(isOn ? 34 - 20 : 0);
        fillAnimation.setToValue(isOn ? colorOn : Color.LIGHTGRAY);
        fillAnimation.setFromValue(isOn ? Color.LIGHTGRAY : colorOn);
        rectangle.setStroke(isOn ? colorOn : Color.LIGHTGRAY);
        animation.setOnFinished(e -> {
            setSwitchedOn(isOn);
            animation.stop();
        });
        animation.play();
    }
}
