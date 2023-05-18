package BoteFx.service;

import BoteFx.model.Token;
import BoteFx.repository.TokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Den 17.09.2022
 */

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;



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
     *  in h2 localBoteFx.mv.db/Token speichern, für weitere nutzung als 'cookie'
     *  in ViewService
     */
    public void saveToken(Token token){
        tokenRepository.save(token);
    }



    /**
     * User Token nicht vorhanden, neu anmelden oder Registrieren...
     *
     * Benutzt: ViewService, Zeile: 54
     *
     * output: 12042023204557
     * output leer: null
     * @return
     */
    public String meinToken(){

        Iterable<Token> result = tokenRepository.findAll();

        if (!result.iterator().hasNext()){
            return null;
        }
        return result.iterator().next().getMytoken();

    }



    /**
     *
     * @param token
     * @return
     */
    public boolean deleteToken(String token){

        Token findToken = tokenRepository.findById(token).orElse(null);

        if (findToken != null){
            tokenRepository.delete(findToken);
        }

        return false;
    }

}
