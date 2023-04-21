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
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private String datum;



    public Token(){
        // Leer
    }

    public Token(String mytoken, String datum){
        this.mytoken    = mytoken;
        this.datum      = datum;
    }


    public String getDatum() { return datum; }
    public void setDatum(String datum) { this.datum = datum; }

    public String getMytoken() { return mytoken; }
    public void setMytoken(String mytoken) { this.mytoken = mytoken; }

    @Override
    public String toString(){
       return "Token{ " +
               ", mytoken='" + mytoken + '\'' +
                ", datum='" + datum + '\'' +
                "}";
    }

}
