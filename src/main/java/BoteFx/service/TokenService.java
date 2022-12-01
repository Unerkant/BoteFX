package BoteFx.service;

import BoteFx.model.Token;
import BoteFx.repository.TokenRepository;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Den 17.09.2022
 */

@Service
public class TokenService {

    @Autowired
    private ConfigService configService;
    @Autowired
    private TokenRepository tokenRepository;
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Neue User Token generieren
     * wird generiert von aktuelle Datum als 14-stellige Zahlen
     * Tag+Monat+Jahr+Stunde+Minute+Sekunde
     *
     * @return
     */
    public String IdentifikationToken(){
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date token = new Date();
        return format.format(token);
    }


    /**
     *  Token ins H2 speichern
     *
     *  Benutzt: MailRegisterController.java Zeile: 160
     *  oder:    TelefonRegisterController Zeile 132
     *
     *  nach erfolgreichen registrierung oder Einloggen den Token
     *  in h2 localBote/Token speichern, für weitere nutzung als 'cookie'
     *  in BoteApp.java Zeile: 55
     */
    public void saveToken(Token token){
        tokenRepository.save(token);
    }


    /**
     *  H2, nach gleichen token suchen
     *  return true: json-array
     *  return false: null
     *  benutzt: MailRegisterController Zeile: 152
     */
    public Token findeToken(String token){
        return tokenRepository.findByMytoken(token);
    }


    /**
     *  Token in json-Datei schreiben:
     *  resources/static/json/token.json
     *
     */
    public String writeToken(String token){
        File file = new File(configService.FILE_URL+"src/main/resources/static/json/token.json");
        if (file.length() == 0) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write("{\"token\":\""+token+"\"}");
                writer.flush();
                writer.close();
                return "writeJsonOk";
            } catch (IOException e) {
                //throw new RuntimeException(e);
                return "writeJsonNein";
            }
        } else {
            return "eintragExist";
        }

    }


    /**
     * Prüfen ob Token vorhanden ist
     * wenn leer ist: null
     * vergleich operation: if (myToken == null){}
     */
    public String tokenHolen(){
        File file =  new File(configService.FILE_URL+"src/main/resources/static/json/token.json");
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(file));
            String jsonToken = String.valueOf(((JSONObject) obj).get("token"));
            return String.valueOf(jsonToken);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return null;
        } catch (ParseException e) {
            //throw new RuntimeException(e);
            return null;
        }

    }

}
