package egovframework.com.config;

import egovframework.com.filter.EgovHtmlTagFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EgovLoginFilter {

    @Bean
    public FilterRegistrationBean<EgovHtmlTagFilter> egovHtmlTagFilter() {
        FilterRegistrationBean<EgovHtmlTagFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new EgovHtmlTagFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
