package BoteFx.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


/**
 * Created by Paul Richter on Sat 5/5/2022
 */

public class Message {

    @Id
    @GeneratedValue
    private long id;
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm:ss")
    private String datum;
    private String freundetoken;
    private String meintoken;
    private String messagetoken;
    private String pseudonym;
    private String vorname;
    private String name;
    @Column(columnDefinition="TEXT")
    private String text;
    private String role;



    public Message() {
        // empty
    }

    public Message(long id, String datum, String fruendetoken, String meintoken, String messagetoken, String pseudonym, String vorname, String name, String text, String role ) {
        this.id = id;
        this.datum = datum;
        this.freundetoken = fruendetoken;
        this.meintoken = meintoken;
        this.messagetoken = messagetoken;
        this.pseudonym = pseudonym;
        this.vorname = vorname;
        this.name = name;
        this.text = text;
        this.role = role;
    }


    public long getId() {
        return id;
    }
    public void setId(long id) { this.id = id; }

    public String getDatum() { return datum; }
    public void setDatum(String datum) { this.datum = datum; }

    public String getFreundetoken() { return freundetoken; }
    public void setFreundetoken(String freundetoken) { this.freundetoken = freundetoken;}

    public String getMeintoken() { return meintoken; }
    public void setMeintoken(String meintoken) { this.meintoken = meintoken; }

    public String getMessagetoken() { return messagetoken; }
    public void setMessagetoken(String messagetoken) { this.messagetoken = messagetoken; }

    public String getPseudonym() {
        return pseudonym;
    }
    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getVorname() { return vorname; }
    public void setVorname(String vorname) { this.vorname = vorname; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", datum='" + datum + '\'' +
                ", freundetoken='" + freundetoken + '\'' +
                ", meintoken='" + meintoken + '\'' +
                ", messagetoken='" + messagetoken + '\'' +
                ", pseudonym='" + pseudonym + '\'' +
                ", vorname='" + vorname + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", role='" + role + '\'' +
                "}";
    }

}

