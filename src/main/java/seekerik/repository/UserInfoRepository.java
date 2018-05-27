package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.company.CompanyInfo;
import seekerik.model.user.UserInfo;

public interface UserInfoRepository extends MongoRepository<UserInfo, String> {
    UserInfo findUserInfoByUserEmail(String email);

}
