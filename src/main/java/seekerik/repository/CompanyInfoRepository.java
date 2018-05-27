package seekerik.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import seekerik.model.company.CompanyInfo;

public interface CompanyInfoRepository extends MongoRepository<CompanyInfo, String> {
    CompanyInfo findCompanyInfoByUserEmail(String email);

}
