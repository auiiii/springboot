package com.zj.resource;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * 无法和jersey-swagger兼容，无法注册，但是接口可以正常访问
 */

@Api(value = "controller格式测试")
@RestController
public class Controller {

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping(value="/hello")
    public Object hello( /* 参数注解 */ @ApiParam(value = "desc of param" , required=true ) @RequestParam String name) {
        String url="http://127.0.0.1:8181/imsp/coordination/dossiers";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
        System.out.println(JSONObject.toJSONString(responseEntity));
        String body = responseEntity.getBody();
        return "Hello " + name + "!";
    }

}
