package seekerik.model.company;


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
@Document(collection = "companyInfo")
public class CompanyInfo {
    @Id
    private String id;
    private String companyInfo;
    private String missionAndVision;
    private String userEmail;
    private String title;
    private String year;
    private String description;
}
