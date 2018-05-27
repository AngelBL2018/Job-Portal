package seekerik.model.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import seekerik.model.company.CompanyInfo;
import seekerik.model.job.Job;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Size(min = 8, max = 15)
    private String password;
    private String userPhone;
    private Gender gender;
    private UserType userType;
    private String profession;
    private String picUrl;
    private String companyName;
    private String companyAddress;
    private String companySize;
    private String industry;
    private String phone;
    private String companyEmail;
    private String companyWebSite;
    private String companyPicUrl;
    private List<Job> wishList;



}
