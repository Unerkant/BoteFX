package BoteFx.service;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    /**
     * layoutContext konfiguriert in BoteApp Zeile 50 (init)
     */
    private ConfigurableApplicationContext configservice;
    public void setConfigservice(ConfigurableApplicationContext layoutContext1){
        this.configservice = configservice;
    }

    /**
     * Globalle Constance deffinieren
     */

    public final String FILE_CSS                = "/static/css/style.css";
    public final String FILE_LEANDER            = "src/main/resources/static/json/laender.json";
    public final String FILE_URL                = "???";
    public final String FILE_HTTP               = "http://localhost:8080/"; /* BOTE PROJECT */
    /* Bild Adresse BEI BOTE:  http://localhost:8080/profilbild/03052022103644.png */

    public final double DEFAULT_WIDTH           = 480;
    public final double DEFAUL_HEIGHT           = 620;
    public final double START_WIDTH             = 680;  /* BoteApp */
    public final double START_HEIGHT            = 700;  /* BoteApp */
    public final double MIN_WIDTH               = 380;  /* BoteApp */
    public final double MIN_HEIGHT              = 550;
    public final double MAX_WIDTH               = 3456; /* Liquid Retina XDR-Display 16-Zoll (3456 × 2234) */
    public final double MAX_HEIGHT              = 2234; /* Liquid Retina XDR-Display 16-Zoll (3456 × 2234) */

    /* Verschidene Einstellungen */
    public  final Integer MESSAGE_LENGTH        = 1000;  /* MessageController */
    public  final Integer TEXTAREA_HEIGHT       = 300;  /* MessageController/Textarea: eingabe text, senden */
    public  final Integer MAIL_LENGTH           = 254;  /* MailLoginController */
    public  final Integer VORWAHL_LENGTH        = 5;    /* TelefonLoginController */
    public  final Integer MAX_TEL_LENGTH        = 15;   /* TelefonLoginController */
    public  final Integer MIN_TEL_LENGTH        = 6;    /* TelefonLoginController */

}