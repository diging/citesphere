package edu.asu.diging.citesphere.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;

import edu.asu.diging.citesphere.config.core.CitesphereConnectionSignUp;
import edu.asu.diging.citesphere.config.core.SimpleSignInAdapter;
import edu.asu.diging.citesphere.core.factory.IZoteroTokenFactory;
import edu.asu.diging.citesphere.core.user.IUserHelper;
import edu.asu.diging.citesphere.core.zotero.IZoteroTokenManager;

@Configuration
@EnableSocial
public class SocialContext implements SocialConfigurer {

    @Autowired
    private IUserHelper userHelper;

    @Autowired
    private ZoteroConnectionFactory zoteroFactory;
    
    @Autowired
    private IZoteroTokenManager tokenManager;
    
    @Autowired
    private IZoteroTokenFactory tokenFactory;
    
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {
        cfConfig.addConnectionFactory(zoteroFactory);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        InMemoryUsersConnectionRepository repository = new InMemoryUsersConnectionRepository(connectionFactoryLocator);
        repository.setConnectionSignUp(new CitesphereConnectionSignUp(userHelper));
        return repository;
    }

    @Bean
    public ProviderSignInController providerSignInController(ConnectionFactoryLocator connectionFactoryLocator,
            UsersConnectionRepository usersConnectionRepository) {
        ProviderSignInController controller = new ProviderSignInController(connectionFactoryLocator,
                usersConnectionRepository, new SimpleSignInAdapter(tokenManager, tokenFactory));
        return controller;
    }
}