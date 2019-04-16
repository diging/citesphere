package edu.asu.diging.citesphere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                // Spring Security ignores request to static resources such as CSS or JS
                // files.
                .ignoring().antMatchers("/static/**");
    }

    @Configuration
    @EnableAuthorizationServer
    public static class OAuth2Configuration extends AuthorizationServerConfigurerAdapter {

        static final String CLIENT_ID = "android-client";
        static final String CLIENT_SECRET = "$2a$04$heXhjyss0lT52E8UZAjIC.y8cor0.SlgTVr8JdVzJ/g6iQyU87zvC";
        static final String GRANT_TYPE_PASSWORD = "password";
        static final String AUTHORIZATION_CODE = "authorization_code";
        static final String REFRESH_TOKEN = "refresh_token";
        static final String IMPLICIT = "implicit";
        static final String SCOPE_READ = "read";
        static final String SCOPE_WRITE = "write";
        static final String TRUST = "trust";
        static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1 * 60 * 60;
        static final int REFRESH_TOKEN_VALIDITY_SECONDS = 6 * 60 * 60;

        @Autowired
        private TokenStore tokenStore;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Bean
        public TokenStore tokenStore() {
            return new InMemoryTokenStore();
        }

        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable().authorizeRequests().antMatchers("/oauth/token").permitAll().anyRequest()
                    .authenticated();
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
            configurer.inMemory().withClient(CLIENT_ID).secret(CLIENT_SECRET)
                    .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)
                    .scopes(SCOPE_READ, SCOPE_WRITE, TRUST).accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
                    .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints// .prefix("/api/v1/")
                    .pathMapping("/oauth/authorize", "/api/v1/oauth/authorize")
                    .pathMapping("/oauth/check_token", "/api/v1/oauth/check_token")
                    .pathMapping("/oauth/confirm_access", "/api/v1/oauth/confirm_access")
                    .pathMapping("/oauth/error", "/api/v1/oauth/error")
                    .pathMapping("/oauth/token", "/api/v1/oauth/token").tokenStore(tokenStore)
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
                    .checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')").allowFormAuthenticationForClients();
        }

    }

    @Configuration
    @Order(1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher()).and().formLogin().loginPage("/")
                    .loginProcessingUrl("/login/authenticate")
                    .failureHandler(customAuthenticationFailureHandler("/?error="))
                    // Configures the logout function
                    .and().logout().deleteCookies("JSESSIONID").logoutUrl("/logout").logoutSuccessUrl("/").and()
                    .exceptionHandling().accessDeniedPage("/403")
                    .and().requestMatchers().requestMatchers(new NegatedRequestMatcher(
                                new OrRequestMatcher(
                                        new AntPathRequestMatcher("/api/**")
                                )
                        ))
                    // Configures url based authorization
                    .and().authorizeRequests()
                    // Anyone can access the urls
                    .antMatchers("/", "/resources/**", "/login/authenticate", "/", "/register", "/logout").permitAll()
                    // .antMatchers("/api/v1/oauth/token").permitAll()
                    // The rest of the our application is protected.
                    //.antMatchers("/api/**").authenticated()
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
            http.csrf().disable().authorizeRequests().antMatchers("/api/**").authenticated().and()
                .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        }

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}