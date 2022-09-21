package BoteFx.repository;

import BoteFx.model.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Den 17.09.2022
 */

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    /**
     * 1. Eine 'save' Methode wird schon benutzt von
     * TokenService Methode: saveToken() Zeile: 412
     *
     * die Methode 'save' ist von Repository generiert
     */

    /**
     * 2. Token aus H2 holen und vergleichen
     * benutzt: BoteApp.java Zeile: 75
     */
    Token findByMytoken(String mytoken);
}
