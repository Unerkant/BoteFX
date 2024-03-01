package BoteFx.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


/**
 * Den 1.05.2023
 */

@Entity
public class Neuemessagecounter {

    @Id
    private String token;
    private String count;


    public Neuemessagecounter(){}


    public Neuemessagecounter(String token, String count){
        this.token = token;
        this.count = count;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getCount() { return count; }
    public void setCount(String count) { this.count = count; }
    
    @Override
    public String toString(){
        return "Count{ " +
                ", countToken='" + token + '\'' +
                ", countZahl='" + count + '\'' +
                "}";
    }
}
