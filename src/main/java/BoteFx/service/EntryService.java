package BoteFx.service;

import BoteFx.model.Entry;
import BoteFx.repository.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Den 24.03.2023
 */

@Service
public class EntryService {
    public static final String LANGUAGE_ENTRY_KEY = "currentLanguage";
    public static final String USE_DIALOG_ENTRY_KEY = "useDialog";

    @Autowired
    private EntryRepository entryRepository;

    public void saveSetting(String key, String value) {
        Entry settingEntry = entryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            settingEntry = new Entry();
            settingEntry.setKey(key);
        }

        settingEntry.setValue(value);
        entryRepository.save(settingEntry);
    }

    public String loadSetting(String key) {
        Entry settingEntry = entryRepository.findById(key).orElse(null);

        if(settingEntry == null) {
            return null;
        }

        return settingEntry.getValue();
    }

    public boolean deleteSetting(String key) {
        Entry settingEntry = entryRepository.findById(key).orElse(null);

        if(settingEntry != null) {
            entryRepository.delete(settingEntry);
            return true;
        }

        return false;
    }
}
