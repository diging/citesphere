package edu.asu.diging.citesphere.config.core;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "edu.asu.diging.citesphere.config, edu.asu.diging.citesphere.config.core, edu.asu.diging.citesphere.core, edu.asu.diging.citesphere.data" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RootConfig {


@Bean(name="messageSource")
public ReloadableResourceBundleMessageSource reloadableResource() {
	ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
	reloadableResourceBundleMessageSource.setBasename("classpath:locale/messages");
	reloadableResourceBundleMessageSource.setFallbackToSystemLocale(false);
	return reloadableResourceBundleMessageSource;
}

@Bean
public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholder =  new PropertySourcesPlaceholderConfigurer();
  propertySourcesPlaceholder.setLocation(new ClassPathResource("creators.properties"));
    return propertySourcesPlaceholder;
}

@Bean(name="usersFile")
public PropertiesFactoryBean usersFile() {
	 PropertiesFactoryBean bean = new PropertiesFactoryBean();
	    bean.setLocation(new ClassPathResource("user.properties"));
	    return bean;
}

@Bean(name="iconsResource")
public PropertiesFactoryBean iconsResource() {
	 PropertiesFactoryBean bean = new PropertiesFactoryBean();
	    bean.setLocation(new ClassPathResource("item_type_icons.properties"));
	    return bean;
}
@Bean(name="labelsResource")
public PropertiesFactoryBean labelsResource() {
	 PropertiesFactoryBean bean = new PropertiesFactoryBean();
	    bean.setLocation(new ClassPathResource("labels.properties"));
	    return bean;
}
@Bean(name="creatorsFile")
public PropertiesFactoryBean creatorsFile() {
	 PropertiesFactoryBean bean = new PropertiesFactoryBean();
	    bean.setLocation(new ClassPathResource("creators.properties"));
	    return bean;
}

@Bean(name="appFile")
public PropertiesFactoryBean appFile() {
	 PropertiesFactoryBean bean = new PropertiesFactoryBean();
	    bean.setLocation(new ClassPathResource("app.properties"));
	    return bean;
}

}
