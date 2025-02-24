package com.baohou;

import junit.framework.TestCase;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.persistence.entity.VariableInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Unit test for simple App.
 */
public class ScriptEventTest extends TestCase {

    private static final ProcessEngine processEngine;

    /**
     * 初始化 flowable 配置
     */
    static {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flowable?zeroDateTimeBehavior=convertToNull&useUnicode=true" +
                        "&characterEncoding=utf8&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
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
                .addClasspathResource("java-script.bpmn20.xml")
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
        Scanner scanner= new Scanner(System.in);

        String employee = "zhangbh";
        Integer nrOfHolidays = 3;
        String description = "take care of parents";

        RuntimeService runtimeService = processEngine.getRuntimeService();

        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", employee);
        variables.put("nrOfHolidays", nrOfHolidays);
        variables.put("description", description);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        // 此时已经流转到脚本节点了
        Boolean scriptVar = (Boolean) runtimeService.getVariable(processInstance.getProcessInstanceId(), "myScriptRsp");
        System.out.println("The scriptTask default result is: " + scriptVar);
    }


    public void p_testComplete() {
        // 查询任务列表
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i=0; i < tasks.size(); i++) {
            System.out.println((i+1) + ") " + tasks.get(i).getName());
        }

        // 完成任务
        System.out.println("Which task would you like to complete?");
        Scanner scanner= new Scanner(System.in);
        int taskIndex = Integer.parseInt(scanner.nextLine());
        Task task = tasks.get(taskIndex - 1);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " wants " +
                processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");

        boolean approved = scanner.nextLine().toLowerCase().equals("y");
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("approved", approved);
        taskService.complete(task.getId(), variables);
    }
}
