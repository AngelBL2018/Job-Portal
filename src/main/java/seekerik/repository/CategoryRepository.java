package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.Category.Category;

public interface CategoryRepository extends MongoRepository<Category,String> {


}
