package BoteFx.service;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Den 5.12.2022
 */

@Service
public class MethodenService {


    /**
     * Aktuelle Datum
     * 1. deDatum() -> für Datenbank speichern
     * 2. Identifikation Token erstellen(nach aktuelle Datum),
     *    Tag+Monat+Jahr+Stunde+Minute+Sekunde
     */
    public String deDatum(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date de = new Date();
        return format.format(de);
    }


    /**
     * E-Mail Adresse Validieren
     *
     * return true/false
     */
    public boolean mailValid(String mail){
        //String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,20})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mail);
        return matcher.matches();
    }


    /**
     * Language International
     */
    public String languagen(String landkurzel){
        switch (landkurzel){
            case "de":              return "+49";
            case "en":              return "+44";
            case "fr":              return "+33";
            case "it":              return "+39";
            default:                return "+49";
        }
    }


    /**
     *  Zufall Color: zusammen gesetzen aus 6 Zahlen oder Buchstaben
     *  "123456789ABCDEF", vorkommt meistens bei hellen Farben
     *
     *  return: z.b.s #B48313
     */
    public String zufallColor(){
        String letters = "123456789ABCDEF";
        SecureRandom random = new SecureRandom();
        StringBuilder color = new StringBuilder("#");
        for (int i = 0; i < 6; i++){
            color.append(letters.charAt(random.nextInt(letters.length())));
        }
        return String.valueOf(color);
    }


    /**
     * HIDE PANE
     *
     * Schliessen eine AnchorPane von unten nach oben, sleep 5 millis
     * Achtung: Die Pane wird zuerst geleert... dann geschlossen
     *
     * benutzt von:
     *  1. FreundeCellController Zeile: 295
     *  2.
     *
     * @param pane
     * @param timer
     */
    public void hidePane(AnchorPane pane, int timer){
        int paneHeight = (int) pane.getHeight();
        pane.getChildren().clear();
        pane.setStyle("-fx-background-color: #FFCCCC;");

        Thread tread = new Thread(){
            public void run() {

                for (int i = paneHeight; i >= 0; i--) {
                    try {
                        Thread.sleep(timer);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                        pane.setPrefHeight(Double.valueOf(i));
                }

            }
        }; tread.start();
    }


    /**
     * SHOW PANE
     *
     * zeigen eine Pane
     *
     * @param pane
     * @param timer
     */
    public void showPane(AnchorPane pane, int timer){
        int paneHeight = (int) pane.getHeight();
        Thread tread = new Thread(){
            public void run(){
                for (int x = 0; x <= paneHeight; x++) {
                    try {
                        Thread.sleep(timer);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    pane.setPrefHeight(Double.valueOf(x));
                }
            }
        }; tread.start();
    }


    /**
     * Stage Fenster mit Maus frei an Bildschirm Positionieren
     */
    private double x = 0;
    private double y = 0;
    @FXML
    public void dragget(MouseEvent event){
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
        //System.out.println("Global dragged");
    }
    @FXML
    public void pressed(javafx.scene.input.MouseEvent event) {
        x = event.getSceneX();
        y = event.getSceneY();
        //System.out.println("Global pressed");
    }


    /**
     * Stage Fenster Schliessen
     */
    public void stageClose(ActionEvent event){
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        System.out.println("Global Stage Close");
    }


}
