package com.zj.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;

@Configuration
public class DataSourceConfig {

    //对象映射配置文件
    @ConfigurationProperties("spring.datasource")
    @Bean
    public DataSource getDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        //1般不建议将数据源属性硬编码到代码中，而应该在配置文件中进行配置
        //druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //设置filters属性值为stat,开启SQL监控
        druidDataSource.setFilters("stat,wall");
        return druidDataSource;
    }

    /**
     * 开启 Druid 数据源内置监控页面
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        StatViewServlet statViewServlet = new StatViewServlet();
        //向容器中注入 StatViewServlet，并将其路径映射设置为 /druid/*
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(statViewServlet, "/druid/*");
        //配置监控页面访问的账号和密码（选配）
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        return servletRegistrationBean;
    }

    /**
     * 向容器中添加 WebStatFilter
     * 开启内置监控中的 Web-jdbc 关联监控的数据
     * @return
     */
    @Bean
    public FilterRegistrationBean druidWebStatFilter() {
        WebStatFilter webStatFilter = new WebStatFilter();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(webStatFilter);
        // 监控所有的访问
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));
        // 监控访问不包括以下路径
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
