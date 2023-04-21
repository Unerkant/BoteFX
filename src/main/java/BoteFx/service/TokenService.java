package BoteFx.service;

import BoteFx.model.Token;
import BoteFx.repository.TokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;


/**
 * Den 17.09.2022
 */

@Service
public class TokenService {

    @Autowired
    private ConfigService configService;
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
     *  H2, nach gleichen token suchen
     *  return true: json-array
     *  return false: null
     *  benutzt: MailRegisterController Zeile: 142
     *  benutzt: TelefonRegisterController Zeile: 127
     */
    public Token findeToken(String token){
        return tokenRepository.findByMytoken(token);
    }



    /**
     * User Token, Haupt Token, wird bei Start die BoteFX-App benutzt,
     * wenn leer, neu anmelden oder Registrieren...
     * Benutzt: ViewService, Zeile: 54
     *
     * output: 12042023204557
     * output leer: null
     * @return
     */
    public String meinToken(){
        String token = null;
        Iterable result = tokenRepository.findAll();
        if (!result.iterator().hasNext()){
            return null;
        }

        Iterator itr = result.iterator();
        while (itr.hasNext()){
            Token tok = (Token)itr.next();
            token = tok.getMytoken();
        }
        return token;
    }



    /**
     * Zurzeit nicht benutzt, Einloggen Datum, funktioniert gut
     *
     * output: 21-04-2023 16:02:02
     * bei leer: null
     *
     * @return
     */
    public String einloggDatum(){

        //String log = null;
        Iterable res = tokenRepository.findAll();
        if(!res.iterator().hasNext()){
            return null;
        }

        /**
         * ACHTUNG: abgekürzte variant, aber wenn Datenbank leer ist, dann ohne
         * obere if Abfrage geht auf totale Fehler, liebe Schleife benutzen,
         * die if Abfrage brachen auf jeden Fall
         */
        return tokenRepository.findAll().iterator().next().getDatum();
     /*   Iterator it = res.iterator();
        while (it.hasNext()){
            Token tok = (Token) it.next();
            log = tok.getDatum();
        }
        return log;*/
    }

}
