package edu.asu.diging.citesphere.config.web;




import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;

import edu.asu.diging.citesphere.core.service.oauth.impl.OAuthClientManager;
import edu.asu.diging.citesphere.web.admin.oauth.AddOAuthClientController;


@Configuration
@ComponentScan(basePackages = { "edu.asu.diging.citesphere.web, edu.asu.diging.citesphere.api, edu.asu.diging.citesphere.config.web" })
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		// TODO Auto-generated method stub

		resolvers.add(sortHandlerMethodArgumentResolver());
		resolvers.add(pageableHandlerMethodArgumentResolver());
	}



	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// TODO Auto-generated method stub
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}



	@Bean(name="sortResolver")
	public SortHandlerMethodArgumentResolver sortHandlerMethodArgumentResolver() {

		return new SortHandlerMethodArgumentResolver();
	}

	@Bean(name="pageableResolver")
	public PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {

		return new PageableHandlerMethodArgumentResolver(sortHandlerMethodArgumentResolver());
	}

	@Bean(name="tilesViewResolver")
	public UrlBasedViewResolver tilesResolver() {

		UrlBasedViewResolver urlBasedViewResolver = new UrlBasedViewResolver();
		urlBasedViewResolver.setViewClass(TilesView.class);

		return urlBasedViewResolver;
	}

	@Bean(name="tilesConfigurer")
	public TilesConfigurer tilesConfigurer() {
		TilesConfigurer tilesConfigurer = new TilesConfigurer();
		tilesConfigurer.setDefinitions(new String[] {"/WEB-INF/tiles-defs.xml"});

		return tilesConfigurer;
	}




}
