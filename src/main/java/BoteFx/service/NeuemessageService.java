package BoteFx.service;

import BoteFx.model.Neuemessage;
import BoteFx.repository.NeuemessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Den 1.05.2023
 */

@Service
public class NeuemessageService {

    @Autowired
    private NeuemessageRepository neuemessageRepository;


    /**
     * eingehende neue Message count speichern...
     * wenn eintrag vorhanden ist, einfach count überschreiben und speichern
     *
     * Parameter: token: token von Freund(wer sendet nachricht)
     *            count: anzahl die Nachricht, FreundeCellController:nachrichtenEmpfangen/ count++
     *
     * @param token
     * @param count
     */
    public void saveCountNeueMessage(String token, String count){

        Neuemessage countmessage = neuemessageRepository.findById(token).orElse(null);

        if (countmessage == null){
            countmessage = new Neuemessage();
            countmessage.setToken(token);
        }

        countmessage.setCount(count);
        neuemessageRepository.save(countmessage);
    }



    /**
     * eingehende neue Message count holen, bei leer zurück NULL
     *
     * Parameter: Freunde Token (wer hatte die Nachricht versendet)
     * output: 1 (Zahl) oder null
     *
     * @param token
     * @return
     */
    public  String loadCountNeueMessage(String token){

        Neuemessage countmessage = neuemessageRepository.findById(token).orElse(null);

        if (countmessage == null){
            return null;
        }

        return countmessage.getCount();
    }


    /**
     * wenn neue Message werden gelesen(click auf die Freunde), der count in datenbank wird gelöscht
     *
     * Parameter: token von freunde
     *
     * @param token
     * @return
     */
    public boolean deleteCountNeueMessage(String token){

        Neuemessage countmessage = neuemessageRepository.findById(token).orElse(null);

        if (countmessage !=null){
            neuemessageRepository.delete(countmessage);
            return true;
        }
        return false;
    }

}
