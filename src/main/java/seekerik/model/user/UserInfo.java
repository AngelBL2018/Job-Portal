package seekerik.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "userInfo")
public class UserInfo {
    @Id
    private String id;
    private String userInfo;
    private String jobTitle;
    private String companyName;
    private String from;
    private String to;
    private String degree;
    private String instituteName;
    private String fromYear;
    private String toYear;
    private String result;
    private String specialQualification;
    private String language;
    private String fullName;
    private String fathersName;
    private String dateOfBirth;
    private String birthPlace;
    private String nationality;
    private String sex;
    private String address;
    private String declaration;
    private String userEmail;
}
