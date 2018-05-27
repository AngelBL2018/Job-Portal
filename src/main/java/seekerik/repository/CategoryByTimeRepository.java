package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.Category.Category;
import seekerik.model.Category.CategoryByTime;

public interface CategoryByTimeRepository extends MongoRepository<CategoryByTime,String> {


}
