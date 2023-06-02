package BoteFx.repository;

import BoteFx.model.Entry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Den 24.03.2023
 */
@Repository
public interface EntryRepository extends CrudRepository<Entry, String> {

}
