package BoteFx.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Den 17.09.2022
 */

@Entity
public class Token {
    @Id
    private String mytoken;



    public Token(){
        // Leer
    }

    public Token(String mytoken){
        this.mytoken    = mytoken;
    }


    public String getMytoken() { return mytoken; }
    public void setMytoken(String mytoken) { this.mytoken = mytoken; }

    @Override
    public String toString(){
       return "Token{ " +
               ", mytoken='" + mytoken + '\'' +
                "}";
    }

}
