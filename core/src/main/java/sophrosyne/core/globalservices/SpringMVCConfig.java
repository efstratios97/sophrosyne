package sophrosyne.core.globalservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sophrosyne.core.globalservices.authservice.AdminApiInterceptor;
import sophrosyne.core.globalservices.authservice.ClientApiInterceptor;
import sophrosyne.core.globalservices.authservice.ExternalApiInterceptor;
import sophrosyne.core.globalservices.authservice.InternalApiInterceptor;
import sophrosyne.core.globalservices.authservice.UserApiInterceptor;

@Configuration
public class SpringMVCConfig implements WebMvcConfigurer {

  @Autowired private InternalApiInterceptor internalApiInterceptor;

  @Autowired private ExternalApiInterceptor externalApiInterceptor;

  @Autowired private AdminApiInterceptor adminApiInterceptor;

  @Autowired private UserApiInterceptor userApiInterceptor;

  @Autowired private ClientApiInterceptor clientApiInterceptor;

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry
        .addInterceptor(externalApiInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns("/auth/**");
    registry
        .addInterceptor(internalApiInterceptor)
        .addPathPatterns("/int/**")
        .excludePathPatterns("/auth/**");
    registry
        .addInterceptor(adminApiInterceptor)
        .addPathPatterns("/int/admin/**")
        .excludePathPatterns("/auth/**");
    registry
        .addInterceptor(userApiInterceptor)
        .addPathPatterns("/int/user/**")
        .excludePathPatterns("/auth/**");
    registry
        .addInterceptor(clientApiInterceptor)
        .addPathPatterns("/int/client/**")
        .excludePathPatterns("/auth/**");
  }
}
