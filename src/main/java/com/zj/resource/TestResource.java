package com.zj.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Api(value = "测试接口")
@Component
@Path("")
public class TestResource {

    @GET
    @Path("/test")
    @ApiOperation(value="test-interface", notes="test-interface")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response test()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("code", 0);
        return Response.ok(map).build();
    }


}
