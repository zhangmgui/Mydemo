package com.xytest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
public class DemoApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
/*	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(DemoApplication.class);
	}*/
	/*@Bean
	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
		ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
		bean.setListener(new ServletContextListener() {
			@Override
			public void contextInitialized(ServletContextEvent servletContextEvent) {

			}

			@Override
			public void contextDestroyed(ServletContextEvent servletContextEvent) {
				Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1).shutdownNow();//销毁线程池
				System.out.println(">>>销毁线程池<<<");
			}
		});
		return bean;
	}*/

	/*@Bean
	public ServletRegistrationBean druidServlet() {
		ServletRegistrationBean druid = new ServletRegistrationBean();
		druid.setServlet(new StatViewServlet());
		druid.setUrlMappings(Arrays.asList("/db_info*//*"));

		Map<String, String> params = new HashMap<>();
		params.put("loginUsername", "admin");
		params.put("loginPassword", "xyt.com");
		druid.setInitParameters(params);
		return druid;
	}*/

/*
	@Bean
	public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean) {
		log.debug("CacheConfiguration.ehCacheCacheManager()");
		return new EhCacheCacheManager(bean.getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		log.debug("CacheConfiguration.ehCacheManagerFactoryBean()");
		EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("/ehcache.xml"));
		cacheManagerFactoryBean.setShared(false);
		return cacheManagerFactoryBean;
	}
*/


}