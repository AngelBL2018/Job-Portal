package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.job.Job;
import seekerik.model.job.WishList;

import java.util.List;

public interface WishListRepository extends MongoRepository<WishList,String> {

List<WishList> findAllByUserId(String id);
WishList findOneByJobAndUserId(Job job,String id);
}
