package BoteFx.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ApiService {

    /**
     * Request Api  an Bote jegliche Art
     * für den registrierung oder neu Anmelden oder Mail+SMS versenden
     *
     * ACHTUNG: kurze Anleitung
     *          // Request & response an/von Bote
     *         String urlApi = configService.FILE_HTTP+"freundeApi";
     *         String paramApi = "{ \"sendToken\":\""+ myToken +"\" }";
     *         HttpResponse<String> response = apiService.requestAPI(urlApi, paramApi);
     *
     *
     * ACHTUNG: Detaillierte Anleitung (senden+empfangen)
     *
     *          REQUEST SENDEN
     * benutzt in BoteFX (App version)
     * 1. MailLoginController.java -> Methode: mailPrufen
     * 2. MailRegisterController.java -> Methode: codePrufen
     * 3. FreundeController -> Methode: freundeLaden
     * 4. FreundeCellController -> Methode: freundRemove
     *
     *      bedienengen für Parameter:
     *      (soll von controller zugesendet sein)
     *      a. apiUrl = URL-Adresse als String: "http://localhost:8080/mailApi"
     *                   oder String link = GlobalConfig.FILE_HTTP+"telefonApi";
     *      b. apiParam = Parameter als Json-Array/object (z.b.s, email, code ...)
     *
     *         z.b.s String json = "{ \"neuUserMail\":"\richterpaul@mail.de\" }";
     *         z.b.s String json = "{ \"neuUserMail\":"+newUserMail+" }";
     *         z.b.s String apiJson = "{\"code\":"+code+", \"token\":"+neuToken+"}";
     *
     *  wenn Fehler kommt: apiTelefon(ApiTelefonController.java:37) ~[classes/:na]
     *         z.b.s String data = "{\"neuUserTelefon\":\""+telefon+"\"}";
     *
     *          REQUEST EMPFANGEN
     * benutzt in Bote (Browser version), Request empfangen
     * 1. ApiMailController -> @PostMapping(value = "/mailApi")
     *                      -> @PostMapping(value = "/codeApi")
     *
     * 2. ApiTelefonController  ->  @PostMapping(value = "/telefonApi")
     *                          ->  @PostMapping(value = "/kodeApi")
     *
     * 3.  ApiFreundeController: (zugesendeten token auslesen)
     *     ...@PostMapping(value = "/freundeRemoveApi")
     *     public ResponseEntity apiFreundeRemove(@RequestBody String sendMessageToken){
     *
     *         JSONObject ob = new JSONObject(sendMessageToken);
     *         String zugesandtMessageToken = (String) ob.get("sendmessagetoken");
     *     }
     *
     */
    public HttpResponse<String> requestAPI(String apiUrl, String apiParam){

        HttpResponse<String> response = null;
        // send a JSON data
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(apiParam))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //return response;

        }catch (IOException ex){
            //ex.printStackTrace();

        }catch (InterruptedException ie){
            //ie.printStackTrace();

        }

        return response;
    }

}
