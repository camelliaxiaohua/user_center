package camellia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类，用于定制化 Spring MVC。
 * 配置 CORS（跨源资源共享）设置。
 *
 * @author Camellia.xioahua
 * @datetime 2024/7/13 下午3:30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置 CORS 映射，允许跨源请求。
     *
     * @param registry 要配置的 CORS 注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许来自特定域的跨源请求
        registry.addMapping("/**")
                // 允许特定来源的请求
                .allowedOrigins("http://localhost:3000")
                // 允许凭证（例如，cookies）
                .allowCredentials(true)
                // 允许特定的 HTTP 方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有的请求头
                .allowedHeaders("*")
                // 设置预检请求的最大缓存时间
                .maxAge(3600);
    }
}
