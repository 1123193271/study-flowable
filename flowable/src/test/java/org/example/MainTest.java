package org.example;

import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.*;
import org.flowable.engine.impl.RuntimeServiceImpl;
import org.flowable.engine.impl.agenda.ContinueProcessOperation;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.HistoricVariableService;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTest {

    ProcessEngineConfiguration configuration = null;
    ProcessEngine processEngine = null;
    RuntimeService runtimeService;
    TaskService taskService;
    RepositoryService repositoryService;
    IdentityService identityService;
    HistoryService historyService;
    @Before
    public void before(){
        // 获取  ProcessEngineConfiguration 对象
        configuration = new StandaloneProcessEngineConfiguration();
        // 配置 相关的数据库的连接信息
        configuration.setJdbcDriver("com.mysql.cj.jdbc.Driver");
        configuration.setJdbcUsername("root");
        configuration.setJdbcPassword("991103Lxy..");
        configuration.setJdbcUrl("jdbc:mysql://192.168.194.138:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true");
        // 如果数据库中的表结构不存在就新建
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        processEngine = configuration.buildProcessEngine();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        repositoryService = processEngine.getRepositoryService();
        identityService = processEngine.getIdentityService();
        historyService = processEngine.getHistoryService();
    }
    /**
     * 部署流程
     */
    @Test
    public void testDeploy(){
        // 配置数据库相关信息 获取 ProcessEngineConfiguration
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://192.168.194.138:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=false")
                .setJdbcUsername("root")
                .setJdbcPassword("991103Lxy..")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        // 部署流程 获取RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()// 创建Deployment对象
                .addClasspathResource("holiday-request.bpmn20.xml") // 添加流程部署文件
                .name("请求流程") // 设置部署流程的名称
                .deploy(); // 执行部署操作
        System.out.println("deployment.getId() = " + deployment.getId());
        System.out.println("deployment.getName() = " + deployment.getName());

    }
    /**
     * 查看流程的定义
     */
    @Test
    public void testDeployQuery() {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://192.168.194.138:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("991103Lxy..")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId("2501").singleResult();
        System.out.println("processDefinition.getId() = " + processDefinition.getId());
        System.out.println("processDefinition.getName() = " + processDefinition.getName());
        System.out.println("processDefinition.getDeploymentId() = " + processDefinition.getDeploymentId());
        System.out.println("processDefinition.getDescription() = " + processDefinition.getDescription());

    }

    @Test
    public void testRunProcess() {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://192.168.194.138:3306/flowable?serverTimezone=UTC&nullCatalogMeansCurrent=true")
                .setJdbcUsername("root")
                .setJdbcPassword("991103Lxy..")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance holidayRequest = runtimeService.startProcessInstanceByKey("holidayRequest");
        System.out.println(holidayRequest.getDeploymentId());
        System.out.println(holidayRequest.getName());
        System.out.println(holidayRequest.getBusinessKey());
    }

    @Test
    public void testExecutionQuery() {
        ProcessEngine processEngine = configuration.buildProcessEngine();
        List<Execution> list = processEngine.getRuntimeService().createExecutionQuery().onlyProcessInstanceExecutions().processInstanceId("10001").list();
        System.out.println(list);

        TaskService taskService = processEngine.getTaskService();
        List<Task> list1 = taskService.createTaskQuery().list();

        IdentityService identityService = processEngine.getIdentityService();

        HistoryService historyService = processEngine.getHistoryService();
        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();

        ManagementService managementService = processEngine.getManagementService();


    }


    @Test
    public void testDeploy2() {
        ProcessEngine processEngine = configuration.buildProcessEngine();
        Deployment dpl = processEngine.getRepositoryService().createDeployment().addClasspathResource("holiday-workflow.bpmn20.xml")
                .name("自定义请假流程")
                .deploy();

        System.out.println(dpl.getId());
    }

    @Test
    public void getProcess() {
        ProcessEngine processEngine = configuration.buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        IdentityService identityService = processEngine.getIdentityService();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testflow");
    }

    @Test
    public void testTask1() {
        List<Task> list = taskService.createTaskQuery().processInstanceId("37501").list();
        System.out.println(list);
        taskService.complete("37507");

    }

    @Test
    public void testSuspendProcess() {
//        runtimeService.suspendProcessInstanceById("15001");
        runtimeService.deleteProcessInstance("15001","测试一下删除流程实例");
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("15001").singleResult();
//        System.out.println(processInstance.isSuspended());
    }

    @Test
    public void testXmlToBpmnModel() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId("holiday-workflow:1:12504").singleResult();
        String resourceName = processDefinition.getResourceName();
        try (InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName)) {
            BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
            XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
            BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xmlStreamReader);
            List<Process> processes = bpmnModel.getProcesses();
            System.out.println(processes);
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testCompleteTask() {
        taskService.complete("1841a91c-d76c-11ee-bebb-7497792a1339");
    }

    @Test
    public void testHistoryVariables() {
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId("9cb6bc88-d6b5-11ee-86c9-7497792a1339")
                .list();
        System.out.println(list);
    }

    @Test
    public void claimTask() {
        /**
         * 认领任务之后，会在act_hi_identitylink表中新增一条数据
         * 与taskID相关，指示这条task的assignee为认领人
         * 也就是说只有历史表才会记录一个任务的assignee
         */
        taskService.claim("757ed512-d6cd-11ee-9350-7497792a1339", "zhangsan");
    }

    @Test
    public void unClaimTask() {
        /**
         * 取回任务之后，act_hi_identitylink表中新增一条数据
         * assignee为空，对应的上一条数据依然不变
         */
        taskService.unclaim("757ed512-d6cd-11ee-9350-7497792a1339");
    }

    @Test
    public void testQueryHistoryAssignee() {
        List<HistoricIdentityLink> historicIdentityLinksForTask = historyService.getHistoricIdentityLinksForTask("757ed512-d6cd-11ee-9350-7497792a1339");
    }

    @Test
    public void testEndFlow() {
        /**
         * 直接删除也会记录endTime,duration
         */
        runtimeService.deleteProcessInstance("7578455b-d6cd-11ee-9350-7497792a1339", "删除candidateworkflow");
        runtimeService.deleteProcessInstance("b373bef7-d6d1-11ee-9350-7497792a1339", "删除candidateworkflow");

    }

    @Test
    public void testCompleteTaskAddVariable() {
        HashMap<String,Object> variables = new HashMap<>();
        List<String> userlist = Arrays.asList("user1","user2","user3");
        variables.put("userlist", userlist);
        taskService.complete("d6d51383-da93-11ee-bc8a-7497792a1339",variables);
    }


    @Test
    public void testCompleteTaskConditionVariables() {
//        HashMap<String,Object> variables = new HashMap<>();
//        variables.put("condition", true);
        taskService.complete("44010ef0-da9b-11ee-bc8a-7497792a1339");
//        ProcessDefinition ddd = repositoryService.getProcessDefinition("ddd");
    }

    @Test
    public void getNextFlowElement() {
        Task task = taskService.createTaskQuery().taskId("1841a91c-d76c-11ee-bebb-7497792a1339").singleResult();
        ExecutionEntity execution = (ExecutionEntity)runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        //当前审批的节点
        String activityId = execution.getActivityId();
        System.out.println(activityId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowElement =(FlowNode) bpmnModel.getFlowElement(activityId);
        List<SequenceFlow> outgoingFlows = flowElement.getOutgoingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement targetFlowElement = outgoingFlow.getTargetFlowElement();
            Class<? extends FlowElement> clazz = targetFlowElement.getClass();
            System.out.println();
        }
    }

    @Test
    public void test() {
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testflow");
//        taskService.setAssignee("25007", "user1");
        taskService.complete("30008");
        HistoricVariableService historicVariableService = CommandContextUtil.getHistoricVariableService();
    }



    //获取连线表达式 ExpressionFactory
    //操作连线表达式




    @Test
    public void testMove() {
        ChangeActivityStateBuilder changeActivityStateBuilder = runtimeService.createChangeActivityStateBuilder();
        changeActivityStateBuilder.processInstanceId("7f824e0a-da0d-11ee-b51e-7497792a1339")
                .moveActivityIdTo("task3","task1").changeState();
    }

    @Test
    public void testActivate() {
        RuntimeServiceImpl runtimeService1 = (RuntimeServiceImpl) runtimeService;
        CommandExecutor commandExecutor = runtimeService1.getCommandExecutor();
        commandExecutor.execute(new Command<Void>() {

            @Override
            public Void execute(CommandContext commandContext) {
                FlowableEngineAgenda agenda = CommandContextUtil.getAgenda();
                ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId("62501").singleResult();
                agenda.planContinueProcessInCompensation(execution);
                return null;
            }
        });
    }

    @Test
    public void testComplete2() {
        ChangeActivityStateBuilder changeActivityStateBuilder = runtimeService.createChangeActivityStateBuilder();
        changeActivityStateBuilder.processInstanceId("7f824e0a-da0d-11ee-b51e-7497792a1339")
                .moveSingleExecutionToActivityIds("72501", Arrays.asList("task2-0", "task1-0")).changeState();
    }

    @Test
    public void testComplete21() {
        ChangeActivityStateBuilder changeActivityStateBuilder = runtimeService.createChangeActivityStateBuilder();
        changeActivityStateBuilder.processInstanceId("cd6d0cdbc-da93-11ee-bc8a-7497792a1339")
                .moveExecutionToActivityId("41f0d0af-da94-11ee-bc8a-7497792a1339", "task2").changeState();
    }

    @Test
    public void testCompleteTask1() {
        taskService.complete("80007");
    }






}