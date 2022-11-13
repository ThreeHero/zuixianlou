package com.threehero.zuixianlou.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.threehero.zuixianlou.common.JacksonObjectMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Slf4j
@Configuration
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig extends WebMvcConfigurationSupport implements WebMvcConfigurer  {

  @Bean
  public Docket createRestApi() {
    // 文档类型
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.threehero.zuixianlou.controller"))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("醉仙楼")
        .version("1.0")
        .description("醉仙楼接口文档")
        .build();
  }

  // 解决跨域
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedHeaders("*")
        .allowedMethods("*")
        .maxAge(1800)
        .allowedOrigins("*");
  }


  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    WebMvcConfigurer.super.configurePathMatch(configurer);
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    WebMvcConfigurer.super.configureContentNegotiation(configurer);
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    WebMvcConfigurer.super.configureAsyncSupport(configurer);
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    WebMvcConfigurer.super.configureDefaultServletHandling(configurer);
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    WebMvcConfigurer.super.addFormatters(registry);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    WebMvcConfigurer.super.addInterceptors(registry);
  }

  /**
   * 设置静态资源映射
   * @param registry
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    log.info("静态资源映射");

    registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
    registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    WebMvcConfigurer.super.addViewControllers(registry);
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    WebMvcConfigurer.super.configureViewResolvers(registry);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
  }

  @Override
  public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
    WebMvcConfigurer.super.addReturnValueHandlers(handlers);
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    WebMvcConfigurer.super.configureMessageConverters(converters);
  }

  /**
   * 扩展mvc框架消息转换器
   * @param converters
   */
  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
    messageConverter.setObjectMapper(new JacksonObjectMapper());
    converters.add(0, messageConverter);
    // WebMvcConfigurer.super.extendMessageConverters(converters);
  }

  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    WebMvcConfigurer.super.configureHandlerExceptionResolvers(resolvers);
  }

  @Override
  public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    WebMvcConfigurer.super.extendHandlerExceptionResolvers(resolvers);
  }

  @Override
  public Validator getValidator() {
    return WebMvcConfigurer.super.getValidator();
  }

  @Override
  public MessageCodesResolver getMessageCodesResolver() {
    return WebMvcConfigurer.super.getMessageCodesResolver();
  }




}
