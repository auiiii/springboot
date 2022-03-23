package com.zj.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 无法和jersey-swagger兼容，无法注册，但是接口可以正常访问
 */

@Api(value = "controller格式测试")
@RestController
public class Controller {

    /* 方法注解 */
    @ApiOperation(value = "hello", notes = "hello")
    @GetMapping(value="/hello")
    public Object hello( /* 参数注解 */ @ApiParam(value = "desc of param" , required=true ) @RequestParam String name) {
        return "Hello " + name + "!";
    }
}
