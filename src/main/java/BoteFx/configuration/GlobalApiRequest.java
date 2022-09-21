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
     *      als Parameter zugesendet von Controller:
     *      a. apiUrl = URL-Adresse: "http://localhost:8080/mailApi"
     *      b. apiParam = Json-Array (z.b.s, email, code ...)
     *         z.b.s String json = "{ \"neuUserMail\":"\richterpaul@mail.de\" }";
     *         z.b.s String json = "{ \"neuUserMail\":"+newUserMail+" }";
     *         z.b.s String apiJson = "{\"code\":"+code+", \"token\":"+neuToken+"}";
     *
     *          REQUEST EMPFANGEN
     * Benutzt in Bote (Browser version), Request empfangen
     * 1. ApiMailController -> @PostMapping(value = "/mailApi")
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
