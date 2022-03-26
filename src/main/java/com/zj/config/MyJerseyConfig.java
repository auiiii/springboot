package com.zj.config;

import com.zj.resource.TestResource;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.AcceptHeaderApiListingResource;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyJerseyConfig extends ResourceConfig {

    @Value("${spring.jersey.application-path}")
    private String apiPath;

    //private Logger LOG = LoggerFactory.getLogger(getClass());

    public MyJerseyConfig()
    {
        //构造函数，在这里注册需要使用的内容，（过滤器，拦截器，API等）
        //LOG.info("Testing record log in Config-Bean");
        register(TestResource.class);
    }

    @PostConstruct
    public void init() {
        this.configureSwagger();
    }

    /**
     * 和springfox的swagger配置不同
     */
    private void configureSwagger() {
        // Available at localhost:port/swagger.json
        this.register(ApiListingResource.class);
        this.register(AcceptHeaderApiListingResource.class);
        this.register(SwaggerSerializers.class);
        BeanConfig config = new BeanConfig();
        config.setConfigId("springboot-jersey-swagger-example");
        config.setTitle("zj demo api document");
        config.setVersion("v1");
        config.setContact("zj");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath(this.apiPath);
        config.setResourcePackage("com.zj.resource");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}
