package BoteFx.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Den 24.03.2023
 */

@Service
public class LanguageService {

    @Autowired
    private EntryService entryService;

    public final static Locale DEFAULT_LANGUAGE = Locale.GERMANY;
    public final static String LOCALIZATION_BUNDLE_NAME = "localization";

    private final ArrayList<Locale> languages;
    private ResourceBundle currentUsedResourceBundle;



    public LanguageService() {
        languages = createAvailableLanguageList();
    }

    public ArrayList<Locale> getAvailableLanguages() {
        return languages;
    }

    public Locale getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    public void setNewLanguage(Locale locale) {
        setCurrentUsedResourceBundle(getLocalizationResourceBundle(locale));
    }

    public String getLocalizationText(String key) {
        return tryConvertISOStringInUTF8(currentUsedResourceBundle.getString(key));
    }

    private String tryConvertISOStringInUTF8(String value) {
        return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public Locale getLastUsedOrDefaultLanguage() {
        String language = entryService.loadSetting(entryService.LANGUAGE_ENTRY_KEY);
        if (language == null) {
            return getDefaultLanguage();
        }

        Locale locale = Locale.forLanguageTag(language);
        if (locale.getLanguage().isEmpty()) {
            return getDefaultLanguage();
        }

        return locale;
    }

    public void saveNewLanguageSetting(Locale newLanguage) {
        entryService.saveSetting(entryService.LANGUAGE_ENTRY_KEY, newLanguage.toLanguageTag());
    }

    private ArrayList<Locale> createAvailableLanguageList() {
        ArrayList<Locale> languages = new ArrayList<>();

        languages.add(Locale.GERMANY);
        languages.add(Locale.US);

        return languages;
    }

    private ResourceBundle getLocalizationResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(LOCALIZATION_BUNDLE_NAME, locale);
    }

    /**
     * start in ViewService Zeile: 79
     *          LayoutService Zeile: 68
     * @return
     */
    public ResourceBundle getCurrentUsedResourceBundle() {
        if (currentUsedResourceBundle == null) {
            setCurrentUsedResourceBundle(getLastUsedOrDefaultResourceBundle());
        }

        return currentUsedResourceBundle;
    }

    private ResourceBundle getLastUsedOrDefaultResourceBundle() {
        try {
            Locale lastUsedOrDefaultLanguage = getLastUsedOrDefaultLanguage();
            if (!getAvailableLanguages().contains(lastUsedOrDefaultLanguage)) {
                throw new NotFoundException("Language is not supported!");
            }

            return ResourceBundle.getBundle(LOCALIZATION_BUNDLE_NAME, lastUsedOrDefaultLanguage);
        } catch (Exception ex) {
            // ignore
        }

        return getDefaultResourceBundle();
    }

    public ResourceBundle getDefaultResourceBundle() {
        return ResourceBundle.getBundle(LOCALIZATION_BUNDLE_NAME, getDefaultLanguage());
    }

    public void setCurrentUsedResourceBundle(ResourceBundle currentUsedResourceBundle) {
        this.currentUsedResourceBundle = currentUsedResourceBundle;
    }
}
