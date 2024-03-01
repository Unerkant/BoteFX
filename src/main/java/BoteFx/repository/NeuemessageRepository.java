package BoteFx.repository;

import BoteFx.model.Neuemessagecounter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeuemessageRepository extends CrudRepository<Neuemessagecounter, String> {
}
