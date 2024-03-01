package BoteFx.configuration;

import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;
import java.util.Set;

/**
 * Den 4.01.2024
 */

@Component
public class BoteConfig {
    private final Properties properties = new Properties();
    private File file = new File("src/main/resources/boteconfig.properties");

    /**
     *                  ALLGEMEINE CODE:
     *
     *      ACHTUNG: nur 3 ausführbare Methoden
     *   <code>
     *      1. getProperties(String key)                // boolean wert von element holen
     *      2. setProperties(String key, String value)  // element neu anlegen oder alte update
     *      3. deleteProperties(String element)         // Löschen von element
     *  </code>
     *
     *      Z.B.S.
     * 1.   abruf von andere Class, einfach key eingeben:
     *      String wert = boteConfig.getProperties("smile");
     *      // output wert: true/false
     *
     * 2.   neue wert speichern, neuen key + neues value eingeben:
     *      boteConfig.setProperties("sticker", "false");
     *
     * 3.   update alten wert, einfach alten key + neue value angeben:
     *      boteConfig.setProperties("smile", "true");
     *      // save: true
     *
     *  4.  Löschen von Elementen
     *      boteConfig.deleteProperties("smile");
     *      // bei nicht existierten element output: null
     *
     *  5. properties array: z.b.s
     *  {firstName=App, lastName=Bote, technology=java, blog=messenger, Gifs=false, smile=false}
     *
     */



    /* <!-- ***************** public Methoden *********************** --> */


    /**
     * properties per getProperties holen,
     * zuerst werte aus den boteconfig holen und in properties speichern
     *
     * @param key
     * @return
     */
    public String getProperties(String key) {

        propertiesLaden();
        return properties.getProperty(key);

    }

    /**
     * neu wert in boteconfig.properties speichern oder alte Update
     * ACHTUNG: zuerst wird aus dem boteconfig ins properties variable aktuelle Werte eingelesen
     *          dann die neue zugesendete werte ins properties einfügt und schliesslich ins
     *          boteconfig.properties speichern
     *
     * @param key
     * @param value
     */
    public void setProperties(String key, String value){

        propertiesLaden();
        properties.put(key, value);
        propertiesSave();

    }


    /**
     * Eines element aus dem properties-Array Löschen
     */
    public void deleteProperties(String element){
        propertiesLaden();
        propertiesLoschen(element);
    }



    /* <!-- *************** Private Methoden *************************** --> */


    /**
     * properties Laden, wird abgerufen bei getProperties auslösung...
     * bei nicht existierten datei: boteconfig.properties output ist: null
     *
     */
    private void propertiesLaden() {

        if (file.exists() && file.isFile() && file.canRead()){

            final InputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //System.out.println("kann nicht Lesen");
        }

    }


    /**
     * die properties-array mit neuen oder geänderten werten ins boteconfig schreiben(save)
     * ACHTUNG: die neue oder geänderte werte in properties-variable werden schon in setProperties eingefügt,
     * hier wird die variable komplekt neu ins boteconfig geschrieben(save)
     *
     * @throws IOException
     */
    private void propertiesSave() {

        if (file.canWrite()) {
            synchronized (this) {

                final OutputStream out;
                try {
                    out = new FileOutputStream(file);
                    properties.store(out, null);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            //System.out.println("kann nicht Schreiben");
        }

    }


    /**
     * einen element aus den properties array löschen
     *
     * ACHTUNG: nach dem Löschen eines element aus dem properties- array müssen sie
     * den neuen properties ins boteconfig.properties wieder speichern
     *
     * bei nicht gefundenen element, output: null
     *
     * @param keys
     */
    private void propertiesLoschen(String keys){

        synchronized (this){
            if (properties.containsKey(keys)) {

                properties.remove(keys);
                propertiesSave();

            } else {
                //System.out.println(" nicht gefunden: " + keys );
            }
        }
    }


    /**
     * Alle properties key ausgeben:
     *
     * Beispiel:
     * Set<String> wert = boteConfig.getAllPropertiesNames();
     *              oder
     * String wert = boteConfig.getAllPropertiesNames().toString();
     *              output
     *         System.out.println("propertiees object: " + wert);
     *         // properties object: [firstName, lastName, technology, blog, smile]
     *
     * @return
     */
    public Set<String> getAllPropertiesNames(){

        propertiesLaden();
        return properties.stringPropertyNames();

    }


    /**
     * Prüfen, ob ein Key existiert im Properties Objekt
     * ein boolean rückgabe
     * die prüfmethode:
     * if (boteConfig.containsKey("blog")){
     *             System.out.println("vorhanden: " + boteConfig.getProperties("blog"));
     *         }
     * @param key
     * @return
     */
    public boolean containsKey(String key){

        propertiesLaden();
        return properties.containsKey(key);

    }
}
