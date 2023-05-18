package BoteFx.repository;

import BoteFx.model.Neuemessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NeuemessageRepository extends CrudRepository<Neuemessage, String> {
}
