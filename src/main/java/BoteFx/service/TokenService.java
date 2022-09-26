package BoteFx.service;

import BoteFx.configuration.GlobalConfig;
import BoteFx.model.Token;
import BoteFx.repository.TokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Den 17.09.2022
 */

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     *  Token ins H2 speichern
     *
     *  Benutzt: MailRegisterController.java Zeile: 160
     *  nach erfolgreichen registrierung oder Einloggen den Token
     *  in h2 localBote/Token speichern, für weitere nutzung als 'cookie'
     *  in BoteApp.java Zeile: 55
     */
    public void saveToken(Token token){
        tokenRepository.save(token);
    }

    /**
     *  nach gleichen token suchen
     *  return true: json-array
     *  return false: null
     *  benutzt: MailRegisterController Zeile: 152
     */
    public Token findeToken(String token){
        return tokenRepository.findByMytoken(token);
    }

    /**
     *  Token in resources/static/json/token.json schreiben
     *
     */
    public String writeToken(String token){
        File file = new File(GlobalConfig.FILE_URL+"src/main/resources/static/json/token.json");
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

}
