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
     * Token aus H2 holen und vergleichen
     */
    Token findByMytoken(String mytoken);

}
