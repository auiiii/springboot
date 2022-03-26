package com.zj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(basePackages = "com.zj.entity")
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "demo running" );
        SpringApplication.run(App.class, args);
    }
}
