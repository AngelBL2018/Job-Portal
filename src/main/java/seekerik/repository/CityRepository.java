package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.City;

 public interface CityRepository extends MongoRepository<City,String> {
}
