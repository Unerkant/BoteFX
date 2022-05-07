package BoteFx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * den 5.5.2022
 */

public class LogService {

    public static Logger getLogger(Class clas) {
        return LoggerFactory.getLogger(clas);
    }

}
