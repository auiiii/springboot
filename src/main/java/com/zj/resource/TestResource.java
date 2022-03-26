package com.zj.resource;

import com.zj.entity.BookDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    //逻辑简单不要service层了
    private BookDao dao;

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

    @GET
    @Path("/book-list")
    @ApiOperation(value="获取书籍列表", notes="获取书籍列表")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookList()
    {
        Map<String,Object> map = new HashMap<>();
        map.put("list", dao.selectAll());
        return Response.ok(map).build();
    }

}
