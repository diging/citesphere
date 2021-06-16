package edu.asu.diging.citesphere.config.core;

import java.util.Arrays;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import edu.asu.diging.citesphere.core.repository.oauth.DbAccessTokenRepository;
import edu.asu.diging.citesphere.core.repository.oauth.DbRefreshTokenRepository;
import edu.asu.diging.citesphere.core.repository.oauth.OAuthClientRepository;
import edu.asu.diging.citesphere.core.service.impl.DbTokenStore;
import edu.asu.diging.citesphere.core.service.oauth.IOAuthClientManager;
import edu.asu.diging.citesphere.core.service.oauth.impl.OAuthClientManager;

@Configuration
@EnableWebSecurity
/*
 * Once spring security provides the new oauth2 authentication provider we can
 * migrate.
 */
@SuppressWarnings("deprecation")
public class SecurityContext extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuthClientRepository clientRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DbAccessTokenRepository accessTokenRepo;

    @Autowired
    private DbRefreshTokenRepository refreshTokenRepo;

    @Value("${_oauth_token_validity}")
    private int oauthTokenValidity;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                // Spring Security ignores request to static resources such as CSS or JS
                // files.
                .ignoring().antMatchers("/static/**");
    }

    @Configuration
    @EnableAuthorizationServer
    @PropertySource("classpath:/config.properties")
    public static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

        @Autowired
        @Qualifier("delegatingAuthenticationManager")
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired
        private ClientDetailsService oauthClientDetails;

        @Autowired
        private OAuth2AuthenticationManager oauthAuthenticationManager;

        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private IOAuthClientManager oauthClientManager;

        @Autowired
        private BCryptPasswordEncoder encoder;

        private WebAuthenticationDetailsSource authenticationDetails = new WebAuthenticationDetailsSource();

        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable().authorizeRequests().antMatchers("/oauth/token").permitAll().anyRequest()
                    .authenticated();
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
            configurer.withClientDetails(oauthClientDetails);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.pathMapping("/oauth/authorize", "/api/oauth/authorize")
                    .pathMapping("/oauth/check_token", "/api/oauth/check_token")
                    // .pathMapping("/oauth/confirm_access", "/api/v1/oauth/confirm_access")
                    .pathMapping("/oauth/error", "/api/oauth/error").pathMapping("/oauth/token", "/api/oauth/token")
                    .tokenStore(tokenStore).userDetailsService(userDetailsService)
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter();
            filter.setAuthenticationDetailsSource(authenticationDetails);
            ClientCredentialsAuthenticationManager manager = new ClientCredentialsAuthenticationManager(
                    oauthClientManager, encoder);
            filter.setAuthenticationManager(manager);

            CitesphereOAuth2AuthenticationProcessingFilter checkFilter = new CitesphereOAuth2AuthenticationProcessingFilter(
                    "/api/oauth/check_token");
            checkFilter.setAuthenticationDetailsSource(authenticationDetails);
            checkFilter.setAuthenticationManager(oauthAuthenticationManager);
            checkFilter.afterPropertiesSet();

            oauthServer.tokenKeyAccess("hasRole('TRUSTED_CLIENT')").allowFormAuthenticationForClients()
                    .checkTokenAccess("hasRole('TRUSTED_CLIENT')")
                    .tokenEndpointAuthenticationFilters(Arrays.asList(new Filter[] { checkFilter, filter }));
        }

    }

    @Configuration
    @Order(1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher()).and().formLogin().loginPage("/")
                    .loginProcessingUrl("/login/authenticate").defaultSuccessUrl("/")
                    .failureHandler(customAuthenticationFailureHandler("/?error="))
                    // Configures the logout function
                    .and().logout().deleteCookies("JSESSIONID").logoutUrl("/logout").logoutSuccessUrl("/").and()
                    .exceptionHandling().accessDeniedPage("/403").and().requestMatchers()
                    .requestMatchers(
                            new NegatedRequestMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/api/**"))))
                    // Configures url based authorization
                    .and().authorizeRequests()
                    .antMatchers("/", "/resources/**", "/login/authenticate", "/login", "/login/reset",
                            "/login/reset/initiated", "/", "/register", "/logout")
                    .permitAll().antMatchers("/password/reset").hasRole("CHANGE_PASSWORD")
                    .antMatchers("/users/**", "/admin/**").hasRole("ADMIN").anyRequest().hasAnyRole("USER", "ADMIN");
        }

        public AuthenticationFailureHandler customAuthenticationFailureHandler(String defaultFailureUrl) {
            return new CustomAuthenticationFailureHandler(defaultFailureUrl);
        }
    }

    @Configuration
    @EnableResourceServer
    @Order(2)
    public static class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {

        private static final String RESOURCE_ID = "my_rest_api";

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.resourceId(RESOURCE_ID).stateless(false);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests().antMatchers("/api/**").authenticated().and().exceptionHandling()
                    .accessDeniedHandler(new OAuth2AccessDeniedHandler());
        }

    }

    @Bean
    public TokenStore tokenStore() {
        return new DbTokenStore(accessTokenRepo, refreshTokenRepo);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    public ClientDetailsService clientDetailsService() {
        return new OAuthClientManager(clientRepo, bCryptPasswordEncoder, oauthTokenValidity);
    }

    @Bean
    public OAuth2AuthenticationManager oauth2AuthenticationManager() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        OAuth2AuthenticationManager manager = new OAuth2AuthenticationManager();
        manager.setClientDetailsService(clientDetailsService());
        manager.setTokenServices(tokenServices);
        return manager;
    }

    @Bean(name = "delegatingAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}