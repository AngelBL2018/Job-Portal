package seekerik.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {

    private seekerik.model.user.User user;

    public CurrentUser(seekerik.model.user.User user) {
        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getUserType().name()));

        this.user = user;


    }

    public seekerik.model.user.User getUser() {
        return user;
    }
}


