package com.baohou;

import junit.framework.TestCase;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.*;

public class LoopEventTest extends TestCase {

    private static final ProcessEngine processEngine;

    /**
     * 初始化 flowable 配置
     */
    static {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable?zeroDateTimeBehavior=convertToNull&useUnicode=true" +
                        "&characterEncoding=utf8&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("12345678")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = cfg.buildProcessEngine();
    }

    /**
     * 部署流程定义
     */
    public void testDeploy() {
        // 部署流程定义
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("loop.bpmn20.xml")
                .deploy();

        // 查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());
    }

    /**
     * 启动流程实例
     */
    public void testStart() {
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // 定义审批人列表
        List<String> approvers = Arrays.asList("manager1", "manager2", "manager3");
        Map<String, Object> variables = new HashMap<>();
        variables.put("approvers", approvers);

        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("multiInstanceUserTaskProcess", variables);
    }
}
