package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.Category.Level;

public interface LevelRepository extends MongoRepository<Level,String> {


}
