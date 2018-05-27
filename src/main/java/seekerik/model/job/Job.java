package seekerik.model.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "jobs")
public class Job {
      @Id
    private String id;
      @NotEmpty
      private String title;
    @NotEmpty
    private String category;
    @NotEmpty
    private String categoryByTime;
    @NotEmpty
    private String address;
    @NotEmpty
    private String city;
    @NotEmpty
    private String postalCode;

    private String salaryInfo;
    @NotEmpty
    private String deadLine;
    @NotEmpty
    private String level;

    private Date published = new Date();

    private String jobFunction;
    private String jobSummary;
    private String keyResponsibilities;
    private String minimumRequirements;
    private String premium;
    private String userEmail;


    private String picUser;
    private String picCompany;




}
