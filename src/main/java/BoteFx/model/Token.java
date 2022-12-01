package BoteFx.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Den 17.09.2022
 */

@Entity
public class Token {
    @Id
    @GeneratedValue
    private long id;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private String datum;
    private String mytoken;


    public Token(){
        // Leer
    }

    public Token(long id, String datum, String mytoken){
        this.id         = id;
        this.datum      = datum;
        this.mytoken    = mytoken;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDatum() { return datum; }
    public void setDatum(String datum) { this.datum = datum; }

    public String getMytoken() { return mytoken; }
    public void setMytoken(String mytoken) { this.mytoken = mytoken; }

    @Override
    public String toString(){
       return "Token{ " +
                "id='" + id + '\''  +
                ", datum='" + datum + '\'' +
                ", mytoken='" + mytoken + '\'' +
                "}";
    }

}
