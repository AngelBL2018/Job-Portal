package seekerik.controller;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import seekerik.model.Category.Category;
import seekerik.model.Category.CategoryByTime;
import seekerik.model.Category.Level;
import seekerik.model.City;
import seekerik.model.company.CompanyInfo;
import seekerik.model.job.Job;
import seekerik.model.job.WishList;
import seekerik.model.user.User;
import seekerik.model.user.UserInfo;
import seekerik.model.user.UserType;
import seekerik.repository.*;
import seekerik.security.CurrentUser;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Controller
public class MainController {


    @Value("${image.dir}")
    String picFolder;

    @Autowired
    UserRepository userRepository;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CompanyInfoRepository companyInfoRepository;
    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CategoryByTimeRepository categoryByTimeRepository;
    @Autowired
    LevelRepository levelRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    WishListRepository wishListRepository;

    @GetMapping("/signIn")
    public String signIn(ModelMap modelMap, @RequestParam(value = "message", required = false) String message) {
        if (message != null) {
            modelMap.addAttribute("message", "Thanks, user was added successfully");
        }
        modelMap.addAttribute("user", new User());

        return "signin";
    }


    @GetMapping("/signUp")
    public String signUpGet(ModelMap modelMap) {


        modelMap.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signUp")
    public String signUpPost(@Valid User user, BindingResult result,
                             ModelMap modelMap, @RequestParam("confirmPassword") String confirmPassword,
                             @RequestParam(value = "picture", required = false) MultipartFile multipartFileForUser,
                             @RequestParam(value = "companyPicture", required = false) MultipartFile multipartFileForCompany) throws IOException {

        if (result.hasErrors()) {
            return "signUp";
        }

        User userExist = userRepository.findUserByEmail(user.getEmail());
        if (userExist != null) {
            modelMap.addAttribute("message", String.format("User with %s already exist", user.getEmail()));
        } else if (confirmPassword != null && confirmPassword.equals(user.getPassword())) {

            if (!multipartFileForUser.getOriginalFilename().equals("")) {
                File file = new File(picFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String imageName = System.currentTimeMillis() + "_" + multipartFileForCompany.getOriginalFilename();
                multipartFileForUser.transferTo(new File(picFolder + imageName));
                user.setPicUrl(imageName);
            }

            if (multipartFileForUser.getOriginalFilename().equals("")) {
                if (user.getGender().name().equals("MALE")) {

                    String imageNameForUser = "man.jpg";

                    user.setPicUrl(imageNameForUser);

                } else {
                    File file = new File(picFolder);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    String imageNameForUser = "woman.jpg";

                    user.setPicUrl(imageNameForUser);


                }
            }


            if (!multipartFileForCompany.getOriginalFilename().equals("")) {
                File file = new File(picFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String imageNameForCompany = System.currentTimeMillis() + "_" + multipartFileForCompany.getOriginalFilename();
                multipartFileForCompany.transferTo(new File(picFolder + imageNameForCompany));
                user.setCompanyPicUrl(imageNameForCompany);
            }
            if (multipartFileForCompany.getOriginalFilename().equals("")) {

                String imageNameForCompany = "company.jpg";
                user.setCompanyPicUrl(imageNameForCompany);
            }
            user.setName(user.getName().trim());
            user.setSurname(user.getSurname().trim());
            user.setEmail(user.getEmail().trim());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUserType(UserType.USER);
            userRepository.save(user);
            return "redirect:/signIn?message=5";
        }

        if (!confirmPassword.equals(user.getPassword())) {
            modelMap.addAttribute("message", "Please repeat password correctly");
        }

        return "/signUp";


    }


    @GetMapping("/addJob")
    public String addJobGet(ModelMap modelMap, @RequestParam(value = "message", required = false) String message) {
        if (message != null) {
            modelMap.addAttribute("message", "Thanks,job was added successfully");
        }

        modelMap.addAttribute("job", new Job());
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("city", cityRepository.findAll());

        return "job-post";


    }


    @PostMapping("/addJob")
    public String addJobPost(@Valid Job job, BindingResult result, ModelMap modelMap, @RequestParam("salaryInfo") String salaryInfo,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = ((CurrentUser) userDetails).getUser();

        if (result.hasErrors()) {

            modelMap.addAttribute("category", categoryRepository.findAll());
            modelMap.addAttribute("city", cityRepository.findAll());
            return "job-post";
        }

        if (!salaryInfo.equals("")) {
            job.setSalaryInfo(salaryInfo);
        }else{
            job.setSalaryInfo("negotiable");
        }


        if (currentUser.getCompanyName().equals("")) {
            job.setPicCompany("");
            job.setPicUser(currentUser.getPicUrl());
        } else {
            job.setPicUser("");
            job.setPicCompany(currentUser.getCompanyPicUrl());
        }

        job.setUserEmail(currentUser.getEmail());
        jobRepository.save(job);
        return "redirect:/addJob?message=5";


    }




    @GetMapping("/index")
    public String index(ModelMap modelMap) {


        List<Category> categories = categoryRepository.findAll();
        for (Category cat: categories) {
            Long countJob = jobRepository.countByCategory(cat.getName());
            cat.setCount(countJob.toString());
            categoryRepository.save(cat);
        }






        modelMap.addAttribute("allJobs", jobRepository.findAll());
        modelMap.addAttribute("category", categories);
        modelMap.addAttribute("cities", cityRepository.findAll());











        return "index";
    }


    @GetMapping("/error404")
    public String error404() {
        return "404";
    }

    @GetMapping("/error500")
    public String error500() {
        return "500";
    }

    @GetMapping("/comingSoon")
    public String comingSoon() {
        return "coming-soon";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }


    @GetMapping("/employeeProfile")
    public String Profile(@AuthenticationPrincipal UserDetails userDetails, ModelMap modelMap,
                          @RequestParam(value = "message", required = false) String message) {
        if (message != null) {
            modelMap.addAttribute("message", "Thanks, your information was updated successfully");
        }

        User currentUser = ((CurrentUser) userDetails).getUser();
        if (currentUser == null) {
            return "redirect:/index";
        }


        modelMap.addAttribute("currentUser", currentUser);
        modelMap.addAttribute("userInfoObject", new UserInfo());
        modelMap.addAttribute("currentUserInfo", userInfoRepository.findUserInfoByUserEmail(currentUser.getEmail()));
    modelMap.addAttribute("wishList", wishListRepository.findAllByUserId(currentUser.getId()));

        return "employee-profile";
    }


    @GetMapping("/jobDetails")
    public String jobDetails(@RequestParam(value = "jobId", required = false) String id, ModelMap modelMap) {

        if (id == null || id.equals("")) {
            return "/error404";
        }
        Job job = jobRepository.findOne(id);
        modelMap.addAttribute("currentJob", job);
        User user = userRepository.findUserByEmail(job.getUserEmail());
        modelMap.addAttribute("jobUser", user);
        return "job-details";
    }


    @GetMapping("/listing")
    public String listing(ModelMap modelMap) {


       List<Job> allJobs = jobRepository.findAll();
        List<Category> category = categoryRepository.findAll();
        List<CategoryByTime> categoryByTime = categoryByTimeRepository.findAll();
        List<City> city = cityRepository.findAll();
       List<Level> level = levelRepository.findAll();



        modelMap.addAttribute("allJobs", allJobs);
        modelMap.addAttribute("category", category);
        modelMap.addAttribute("categoryByTime", categoryByTime);
        modelMap.addAttribute("level", level);
        modelMap.addAttribute("city", city);

        modelMap.addAttribute("totalJob",allJobs.size() );


        return "listing";
    }


    @GetMapping("/notification")
    public String notification() {
        return "notification";
    }

    @GetMapping("/pricing")
    public String pricing() {
        return "pricing";
    }

    @GetMapping("/viewCompnay")
    public String viewCompnay() {
        return "view-compnay";
    }

    @GetMapping("/viewResume")
    public String viewResume() {
        return "view-resume";
    }

    @GetMapping("/profileMark")
    public String miniProfile(@AuthenticationPrincipal UserDetails userDetails, ModelMap modelMap, @RequestParam(value = "message", required = false) String message) {
        User user = ((CurrentUser) userDetails).getUser();
        if (user.getCompanyName().trim().equals("")) {
            return "redirect:/employeeProfile";
        }

        if (message != null) {
            modelMap.addAttribute("message", "Thanks,Company Information was updated successfully");
        }

        modelMap.addAttribute("currentUser", user);
        modelMap.addAttribute("companyInfoObject", new CompanyInfo());
        modelMap.addAttribute("currentCompanyInfo", companyInfoRepository.findCompanyInfoByUserEmail(user.getEmail()));

        return "profileMark";
    }


    @GetMapping(value = "/image")
    public void image(HttpServletResponse response, @RequestParam("fileName") String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(picFolder + fileName);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(inputStream, response.getOutputStream());

    }

    @PostMapping(value = "/changeProfilePhoto")
    public String changeProfilePhoto(@RequestParam(value = "picUrlUser", required = false) MultipartFile multipartFileUser,
                                     @RequestParam(value = "picUrlCompany", required = false) MultipartFile multipartFileCompany,
                                     @AuthenticationPrincipal UserDetails userDetails, ModelMap modelMap) throws IOException {


        String imageNameForUser = "";
        String imageNameForCompany = "";
        int count = 0;
        User user = ((CurrentUser) userDetails).getUser();
        if (multipartFileUser != null && !multipartFileUser.getOriginalFilename().equals("")) {
            File file = new File(picFolder);
            if (!file.exists()) {
                file.mkdirs();
            }
            imageNameForUser = System.currentTimeMillis() + "_" + multipartFileUser.getOriginalFilename();
            multipartFileUser.transferTo(new File(picFolder + imageNameForUser));
            count++;
            user.setPicUrl(imageNameForUser);
            userRepository.save(user);
        }
        if (multipartFileCompany != null && !multipartFileCompany.getOriginalFilename().equals("")) {
            File file = new File(picFolder);
            if (!file.exists()) {
                file.mkdirs();
            }
            imageNameForCompany = System.currentTimeMillis() + "_" + multipartFileCompany.getOriginalFilename();
            multipartFileCompany.transferTo(new File(picFolder + imageNameForCompany));
            count += 2;
            user.setCompanyPicUrl(imageNameForCompany);
            userRepository.save(user);
        }


        List<Job> jobsByEmail = jobRepository.findAllByUserEmail(user.getEmail());
        for (Job job : jobsByEmail) {

            if (!job.getPicCompany().equals("") && !imageNameForCompany.equals("")) {
                job.setPicCompany(imageNameForCompany);
            } else if (!job.getPicUser().equals("") && !imageNameForUser.equals("")) {
                job.setPicUser(imageNameForUser);
            }
            jobRepository.save(job);
        }


        if (count == 1) {
            modelMap.addAttribute("currentUser", user);
            modelMap.addAttribute("userInfoObject", new UserInfo());
            modelMap.addAttribute("currentUserInfo", userInfoRepository.findUserInfoByUserEmail(user.getEmail()));
            return "employee-profile";
        }

        modelMap.addAttribute("companyInfoObject", new CompanyInfo());
        modelMap.addAttribute("currentCompanyInfo", companyInfoRepository.findCompanyInfoByUserEmail(user.getEmail()));
        modelMap.addAttribute("currentUser", user);
        return "profileMark";


    }


    @PostMapping("/addCompanyInfo")
    public String addCompanyInfo(@ModelAttribute("companyInfo") CompanyInfo companyInfo,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = ((CurrentUser) userDetails).getUser();

        CompanyInfo currentInfo = companyInfoRepository.findCompanyInfoByUserEmail(user.getEmail());
        if (currentInfo != null) {
            currentInfo.setCompanyInfo(companyInfo.getCompanyInfo());
            currentInfo.setMissionAndVision(companyInfo.getMissionAndVision());
            currentInfo.setTitle(companyInfo.getTitle());
            currentInfo.setYear(companyInfo.getYear());
            currentInfo.setDescription(companyInfo.getDescription());
            companyInfoRepository.save(currentInfo);
        } else {
            companyInfo.setUserEmail(user.getEmail());
            companyInfoRepository.save(companyInfo);
        }

        return "redirect:/profileMark?message=5";

    }


    @PostMapping("/addUserInfo")
    public String addUserInfo(@ModelAttribute("userInfo") UserInfo userInfo,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = ((CurrentUser) userDetails).getUser();

        UserInfo currentInfo = userInfoRepository.findUserInfoByUserEmail(user.getEmail());
        if (currentInfo != null) {
            currentInfo.setUserInfo(userInfo.getUserInfo());
            currentInfo.setJobTitle(userInfo.getJobTitle());
            currentInfo.setCompanyName(userInfo.getCompanyName());
            currentInfo.setFrom(userInfo.getFrom());
            currentInfo.setTo(userInfo.getTo());
            currentInfo.setDegree(userInfo.getDegree());
            currentInfo.setInstituteName(userInfo.getInstituteName());
            currentInfo.setFromYear(userInfo.getFromYear());
            currentInfo.setToYear(userInfo.getToYear());
            currentInfo.setResult(userInfo.getResult());
            currentInfo.setSpecialQualification(userInfo.getSpecialQualification());
            currentInfo.setLanguage(userInfo.getLanguage());
            currentInfo.setFullName(userInfo.getFullName());
            currentInfo.setFathersName(userInfo.getFathersName());
            currentInfo.setDateOfBirth(userInfo.getDateOfBirth());
            currentInfo.setBirthPlace(userInfo.getBirthPlace());
            currentInfo.setNationality(userInfo.getNationality());
            currentInfo.setSex(userInfo.getSex());
            currentInfo.setAddress(userInfo.getAddress());
            currentInfo.setDeclaration(userInfo.getDeclaration());
            userInfoRepository.save(currentInfo);


        } else {
            userInfo.setUserEmail(user.getEmail());
            userInfoRepository.save(userInfo);
        }

        return "redirect:/employeeProfile?message=5";

    }


    @GetMapping("/deleteAccount")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        User user = ((CurrentUser) userDetails).getUser();
        userRepository.delete(user);
        request.getSession().invalidate();

        return "redirect:/index";

    }


    @GetMapping("/jobFilter")
    public String jobFilter(@RequestParam(value = "category",required = false) String category,
                            @RequestParam(value = "level",required = false) String level,
                            @RequestParam(value = "salaryInfo",required = false) String salaryInfo,
                            @RequestParam(value = "city", required = false) String city,
                            @RequestParam(value = "location", required = false) String location,
                            @RequestParam(value = "jobKeyword", required = false) String jobKeyword, ModelMap modelMap) {

        List<Job> forSalaryInfo=new LinkedList<>();

        if (location != null || jobKeyword != null){

            if(!location.equals("")&& jobKeyword.equals("")){
                List<Job> jobByLocation = jobRepository.findAllByCity(location);
                modelMap.addAttribute("filteredJob", jobByLocation);
                modelMap.addAttribute("totalJob", jobByLocation.size());
            }

        }else {


            if (category != null && level == null && salaryInfo == null && city == null) {
                List<Job> filteredJob = jobRepository.findAllByCategory(category);

                modelMap.addAttribute("filteredJob", filteredJob);
                modelMap.addAttribute("totalJob", filteredJob.size());
            } else {


                if (salaryInfo.equals("negotiable")) {
                    List<Job> jobFilter = jobRepository.findAllByCategoryAndCityAndLevelAndSalaryInfo(category, city, level, salaryInfo);
                    modelMap.addAttribute("filteredJob", jobFilter);
                    modelMap.addAttribute("totalJob", jobFilter.size());
                } else {

                    String[] salary = salaryInfo.split("-");

                    List<Job> jobFilter = jobRepository.findAllByCategoryAndCityAndLevel(category, city, level);

                    for (Job job : jobFilter) {
                        if (Long.parseLong(job.getSalaryInfo()) >= Long.parseLong(salary[0]) && Long.parseLong(job.getSalaryInfo()) <= Long.parseLong(salary[1])) {
                            forSalaryInfo.add(job);
                        }
                    }


                    modelMap.addAttribute("filteredJob", forSalaryInfo);
                    modelMap.addAttribute("totalJob", forSalaryInfo.size());

                }
            }
        }

        List<Job> allJobs = jobRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        List<CategoryByTime> categoryByTime = categoryByTimeRepository.findAll();
        List<City> cities = cityRepository.findAll();
        List<Level> levels = levelRepository.findAll();



        modelMap.addAttribute("allJobs", allJobs);
        modelMap.addAttribute("category", categories);
        modelMap.addAttribute("categoryByTime", categoryByTime);
        modelMap.addAttribute("level", levels);
        modelMap.addAttribute("city", cities);




            return "listingForSearch";
    }



        @GetMapping("/addToWishList")
    public String addToWishList(@RequestParam("jobId")String jobId, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request){

            User user = ((CurrentUser) userDetails).getUser();
            String userId = user.getId();
            Job job = jobRepository.findOne(jobId);

            if (wishListRepository.findOneByJobAndUserId(job, userId) == null){
                wishListRepository.save(WishList.builder()
                        .job(job)
                        .userId(userId)
                        .build());
            }

        return "redirect:/listing";

    }

}
