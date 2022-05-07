package BoteFx.model;

/**
 * Created by Paul Richter on Sat 5/5/2022
 */

public class Message {

    private int id;

    private String datum;

    private String meintoken;

    private String messagetoken;

    private String pseudonym;

    private String vorname;

    private String name;

    private String role;

    private String text;



    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDatum() {
        return datum;
    }
    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getMeintoken() {
        return meintoken;
    }
    public void setMeintoken(String meintoken) {
        this.meintoken = meintoken;
    }

    public String getMessagetoken() {
        return messagetoken;
    }
    public void setMessagetoken(String messagetoken) {
        this.messagetoken = messagetoken;
    }

    public String getPseudonym() {
        return pseudonym;
    }
    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getVorname() {
        return vorname;
    }
    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

}

