package faang.school.postservice.config.context;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<UserHeaderFilter> registrationHeaderFilter() {
        FilterRegistrationBean<UserHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserHeaderFilter(new UserContext()));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
