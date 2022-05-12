package com.zj.processes;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.InputStream;

@Configuration
public class FlowableInitRunner implements CommandLineRunner {

    @Resource
    private RepositoryService repositoryService;


    /**
     * 启动时自动deploy
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        InputStream inputStream = FlowableInitRunner.class.getResourceAsStream("/processes/test_flow.bpmn20.xml");
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addInputStream("test_flow.bpmn20.xml", inputStream);
        deploymentBuilder.deploy();
        System.out.println("flowable-process-deploy-done");
    }
}
