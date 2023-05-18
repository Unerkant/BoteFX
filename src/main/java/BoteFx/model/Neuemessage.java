package BoteFx.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Den 1.05.2023
 */

@Entity
public class Neuemessage {

    @Id
    private String token;
    private String count;


    public Neuemessage(){}


    public Neuemessage(String token, String count){
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
