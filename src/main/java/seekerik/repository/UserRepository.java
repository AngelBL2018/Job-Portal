package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.user.User;

public interface UserRepository extends MongoRepository<User,String> {

    User findUserByEmail(String email);


}
