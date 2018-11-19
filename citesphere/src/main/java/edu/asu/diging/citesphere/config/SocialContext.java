package edu.asu.diging.citesphere.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;

import edu.asu.diging.citesphere.core.user.IUserHelper;

@Configuration
@EnableSocial
@PropertySource("classpath:/config.properties")
public class SocialContext implements SocialConfigurer {
    
    @Value("${_zotero_client_secret}")
    private String zoteroSecret;
    
    @Value("${_zotero_client_key}")
    private String zoteroKey;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private IUserHelper userHelper;
    
     @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig,
            Environment env) {
        ZoteroConnectionFactory zoteroFactory = new ZoteroConnectionFactory(zoteroKey, zoteroSecret);
        cfConfig.addConnectionFactory(zoteroFactory);
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(
            ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(
                dataSource, connectionFactoryLocator, Encryptors.noOpText());
        repository.setConnectionSignUp(new GilesConnectionSignUp(userHelper));
        return repository;
    }
    
    @Bean
    public ProviderSignInController providerSignInController(
            ConnectionFactoryLocator connectionFactoryLocator,
            UsersConnectionRepository usersConnectionRepository) {
        ProviderSignInController controller = new ProviderSignInController(
                connectionFactoryLocator, usersConnectionRepository,
                new SimpleSignInAdapter());
        return controller;
    }
}