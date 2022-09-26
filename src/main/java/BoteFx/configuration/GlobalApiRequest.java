package BoteFx.configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GlobalApiRequest {

    /**
     * Request Api für den registrierung oder neu Anmelden
     *
     *          REQUEST SENDEN
     * Benutzt in BoteFX (App version)
     * 1. MailLoginController.java -> Methode: mailPrufen
     * 2. MailRegisterController.java -> Methode: codePrufen
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
     * Benutzt in Bote (Browser version), Request empfangen
     * 1. ApiMailController -> @PostMapping(value = "/mailApi")
     *                      -> @PostMapping(value = "/codeApi")
     *
     * 2. ApiTelefonController ->  @PostMapping(value = "/telefonApi")
     *
     */
    public static HttpResponse<String> requestAPI(String apiUrl, String apiParam){

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
