package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.job.Job;

import java.util.List;

public interface JobRepository extends MongoRepository<Job,String> {
    List<Job> findAllByUserEmail(String email);
    List<Job> findAllByCategory(String category);
    List<Job> findAllByCategoryAndCityAndLevel(String category,String city, String level);
    List<Job> findAllByCategoryAndCityAndLevelAndSalaryInfo(String category,String city, String level, String salary);
    List<Job> findAllByCity(String city);
 //   List<Job> findAllByTitleLike(String city);
    Long countByCategory(String category);


}
