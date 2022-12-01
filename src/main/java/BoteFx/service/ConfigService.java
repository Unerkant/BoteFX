package BoteFx.service;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
@Service
public class ConfigService {

    /**
     * Globalle Constance deffinieren
     */

    public static final String FILE_CSS         = "/static/css/style.css";
    public static final String FILE_URL         = "/Users/paulrichter/ideaProject/BoteFX/";
    public static final String FILE_HTTP        = "http://localhost:8080/"; /* BOTE PROJECT */
    /* Bild Adresse BEI BOTE:  http://localhost:8080/profilbild/03052022103644.png */
    public static final double DEFAULT_WIDTH    = 480;
    public static final double DEFAUL_HEIGHT    = 620;
    public static final double MIN_WIDTH        = 380;  /* BoteApp */
    public static final double MIN_HEIGHT       = 700;  /* BoteApp */
    public static final double MAX_WIDTH        = 650;
    public static final double START_WIDTH      = 480;
    public static final double START_HEIGHT     = 720;

    /* Verschidene Einstellungen */
    public  final Integer MAIL_LENGTH           = 254;    /* MailLoginController */
    public  final Integer VORWAHL_LENGTH        = 5;    /* TelefonLoginController */
    public  final Integer MAX_TEL_LENGTH        = 11;    /* TelefonLoginController */
    public  final Integer MIN_TEL_LENGTH        = 6;    /* TelefonLoginController */


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