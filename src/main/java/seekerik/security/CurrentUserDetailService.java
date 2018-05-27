package seekerik.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import seekerik.model.user.User;
import seekerik.repository.UserRepository;


@Service
public class CurrentUserDetailService implements UserDetailsService {


    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User userByEmail = userRepository.findUserByEmail(s);
        if (userByEmail == null){
            throw new UsernameNotFoundException(String.format("User wuth %s not found",s));
        }
        return new CurrentUser(userByEmail);
    }
}
