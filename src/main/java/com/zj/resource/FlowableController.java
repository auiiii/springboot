package com.zj.resource;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 真正模拟审批流程的rest类
 * 可进行正向、回退、终止等流程
 */
@RestController
@RequestMapping("flowable")
public class FlowableController {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private ProcessEngine processEngine;


    /**
     * 启动一个流程
     * map中的key就是流程变量，后面的方法可以取出来使用。
     * @param taskNo
     * @param userId
     * @return
     */
    @GetMapping("add")
    public String addExpense(String taskNo, String userId)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("taskNo", taskNo);
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("test_flow" ,map);
        return "提交成功,id为:" + instance.getId();
    }

    /**
     * 通过/拒绝任务，对应网关
     *
     * @param taskId
     * @param approved 1 ：true  2：false
     * @return
     */
    @GetMapping("apply")
    public String apply(String taskId, String approved) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return "流程不存在";
        }
        Map<String, Object> variables = new HashMap<>();
        String apply = approved.equals("1") ? "200" : "500";
        variables.put("status", apply);
        taskService.complete(taskId, variables);
        return "审批是否通过：" + approved;
    }

    /**
     * 终止流程实例
     *
     * @param processInstanceId
     */
    @GetMapping("deleteInstance")
    public String deleteProcessInstanceById(String processInstanceId) {
        // ""这个参数本来可以写删除原因
        runtimeService.deleteProcessInstance(processInstanceId, "");
        return "终止流程实例成功";
    }


    /**
     * 获取指定用户组流程任务列表
     *
     * @param group
     * @return
     */
    @GetMapping("list")
    public Object list(String group) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(group).list();
        return tasks.toString();
    }

    /**
     * 驳回流程实例
     * @param taskId
     * @param targetTaskKey,对应xml该阶段的id名称，如register
     * @return
     */
    @GetMapping("rollback")
    public String rollbackTask(String taskId, String targetTaskKey) {
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (currentTask == null) {
            return "节点不存在";
        }
        List<String> key = new ArrayList<>();
        key.add(currentTask.getTaskDefinitionKey());

        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(currentTask.getProcessInstanceId())
                .moveActivityIdsToSingleActivityId(key, targetTaskKey)
                .changeState();
        return "驳回成功...";
    }

    /**
     * 查询当前流程进展图
     *
     * @param httpServletResponse
     * @param processId
     * @throws Exception
     */
    @RequestMapping(value = "processDiagram")
    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
