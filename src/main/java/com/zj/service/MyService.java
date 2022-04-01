package com.zj.service;

import com.alibaba.fastjson.JSONObject;
import com.zj.entity.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class MyService {

    @Autowired
    private BookDao dao;

    @Autowired
    @Qualifier("MyThreadPoolExecutor")
    private ThreadPoolExecutor excutor;

    @Transactional
    public String selectAll() throws Exception
    {
        //submit是支持runable和callable的，但是execute是仅支持runable
        Future future = excutor.submit(new Callable<Object>() {
            @Override
            public Object call()
            {
                return dao.selectAll();
            }
        });
        return JSONObject.toJSONString(future.get());
    }


}
