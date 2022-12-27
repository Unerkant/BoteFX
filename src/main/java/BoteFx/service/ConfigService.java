package BoteFx.service;

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
    public  final Integer MESSAGE_LENGTH        = 500;   /* MessageController */
    public  final Integer MAIL_LENGTH           = 254;  /* MailLoginController */
    public  final Integer VORWAHL_LENGTH        = 5;    /* TelefonLoginController */
    public  final Integer MAX_TEL_LENGTH        = 15;   /* TelefonLoginController */
    public  final Integer MIN_TEL_LENGTH        = 6;    /* TelefonLoginController */

}