package BoteFx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * Den 24.03.2023
 */

@Entity
public class Entry {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String land;

    @Column(nullable = false)
    private String language;

    public Entry() {

    }

    public Entry(Long id, String land, String language){
        this.id     = id;
        this.land    = land;
        this.language  = language;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getKey() {
        return land;
    }
    public void setKey(String key) {
        this.land = key;
    }

    public String getValue() {
        return language;
    }
    public void setValue(String value) {
        this.language = value;
    }

    @Override
    public String toString(){
        return "Language{ " +
                "id='" + id + '\''  +
                ", land='" + land + '\'' +
                ", language='" + language + '\'' +
                "}";
    }
}
