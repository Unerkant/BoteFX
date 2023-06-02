package BoteFx.service;

import BoteFx.model.Usern;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * Den 27.04.2023
 */

@Service
public class UserdataService {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ApiService apiService;


    /**
     * Meine Daten aus der Datenbank Bote/ApiUser/ @PostMapping(value = "/userApi") holen
     *
     * zugesendeten Array, Meine Daten:
     *           {"id":362,"token":"12042023204557","datum":"12-04-2023 20:45:57","bild":"","pseudonym":"MA",
     *          "name":"","vorname":"","email":"macbookpro@mail.com","telefon":"","role":"default","other":""}
     *
     * @return
     */
    public Usern meineDaten(){

        String myToken = tokenService.meinToken();

        String userUrl  = configService.FILE_HTTP+"userApi";
        String userJson = "{\"userToken\":\""+myToken+"\"}";
        HttpResponse<String> response = apiService.requestAPI(userUrl, userJson);

        ObjectMapper mapper = new ObjectMapper();
        try {
            Usern myData = mapper.readValue(response.body(), Usern.class);
            return myData;

        } catch (IOException e) {
            //throw new RuntimeException(e);
            return null;
        }
    }

}
