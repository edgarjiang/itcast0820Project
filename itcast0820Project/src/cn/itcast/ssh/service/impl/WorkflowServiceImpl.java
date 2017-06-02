package cn.itcast.ssh.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;

import cn.itcast.ssh.dao.ILeaveBillDao;
import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.service.IWorkflowService;
import cn.itcast.ssh.utils.SessionContext;
import cn.itcast.ssh.web.form.WorkflowBean;

public class WorkflowServiceImpl implements IWorkflowService {

	/**请假申请Dao*/
	private ILeaveBillDao leaveBillDao;
	
	private RepositoryService repositoryService;
	
	private RuntimeService runtimeService;
	
	private TaskService taskService;
	
	private FormService formService;
	
	private HistoryService historyService;
	
	public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
		this.leaveBillDao = leaveBillDao;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
	
	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	@Override
	public void saveNewDeploye(File file, String filename) {
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			repositoryService.createDeployment()
			.name(filename)
			.addZipInputStream(zipInputStream)
			.deploy();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = repositoryService.createDeploymentQuery()
				.orderByDeploymenTime().asc()
				.list();

		return list;
	}

	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
				.orderByProcessDefinitionVersion().asc()
				.list();
		return list;
	}

	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}

	@Override
	public void saveStartProcess(WorkflowBean workflowBean) {
		Long id = workflowBean.getId();
		LeaveBill leaveBill = this.leaveBillDao.findLeaveBillById(id);
		leaveBill.setState(1);
		//this.leaveBillDao.updateLeaveBill(leaveBill);
		
		String key = leaveBill.getClass().getSimpleName();
		
		Map<String,Object> variables = new HashMap<String, Object>();
		variables.put("inputUser", SessionContext.get().getName());
		
		String objId = key + "." + id;
		variables.put("objId", objId);
		//runtimeService.startProcessInstanceByKey(key, variables);
		runtimeService.startProcessInstanceByKey(key, objId, variables);
		
	}

	@Override
	public List<Task> findTaskListByName(String name) {
		List<Task> list = taskService.createTaskQuery()
		.taskAssignee(name)
		.orderByTaskCreateTime().asc()
		.list();
		
		return list;
	}

	@Override
	public String findTaskFormKeyByTaskId(String taskId) {
		TaskFormData  formData = formService.getTaskFormData(taskId);
		String url = formData.getFormKey();
		return url;
	}

	@Override
	public LeaveBill findLeaveBillByTaskId(String taskId) {
		Task task = taskService.createTaskQuery()
				.taskId(taskId).singleResult();
		String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
			.processInstanceId(processInstanceId)
			.singleResult();
		String businessKey = pi.getBusinessKey();
		String id = "";
		if (StringUtils.isNotBlank(businessKey)) {
			id = businessKey.split("\\.")[1];
					
		}
		LeaveBill leaveBill = leaveBillDao.findLeaveBillById(Long.parseLong(id));
		return  leaveBill;
	}

	@Override
	public List<String> findOutcomeListByTaskId(String taskId) {
		List<String> list = new ArrayList<>();
		
		Task task = taskService.createTaskQuery()
				.taskId(taskId).singleResult();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		
		String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId)
				.singleResult();
		
		String activityId = pi.getActivityId();
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if (pvmList !=null && pvmList.size() >0) {
			for(PvmTransition pvm:pvmList) {
				String name = (String) pvm.getProperty("name");
				if (StringUtils.isNotEmpty(name)) {
					list.add(name);
				}
				else {
					list.add("默认提交");
				}
			}
		}
		return list;
	}
	
	/****/
	@Override
	public void saveSubmitTask(WorkflowBean workflowBean) { 
		String taskId = workflowBean.getTaskId();
		String outcome = workflowBean.getOutcome();
		Long id = workflowBean.getId();
		
		Task task = taskService.createTaskQuery()
				.taskId(taskId)
				.singleResult();
		String processInstanceId = task.getProcessInstanceId();
		String message = workflowBean.getComment();
		Authentication.setAuthenticatedUserId(SessionContext.get().getName());
		taskService.addComment(taskId, processInstanceId, message);
		
		Map<String, Object> variables = new HashMap<String, Object>();
		if (null != outcome && !outcome.equals("默认提交")) {
			variables.put("outcome", outcome);
		}
		
		taskService.complete(taskId, variables);
		
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId)
				.singleResult();
		if (null == pi) {
			LeaveBill bill = leaveBillDao.findLeaveBillById(id);
			bill.setState(2);
		}
	}

	/** 获取批注信息,传递的是当前任务的Id，获取历史Id的批注 **/
	@Override
	public List<Comment> findCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		Task task =  taskService.createTaskQuery()
				.taskId(taskId)
				.singleResult();
		String processInstanceId = task.getProcessInstanceId();
//		List<HistoricTaskInstance> htiList =  historyService.createHistoricTaskInstanceQuery()
//				.processInstanceId(processInstanceId)
//				.orderByTaskDueDate().asc()
//				.list();
//		
//		if (htiList != null && htiList.size()>0) {
//			for(HistoricTaskInstance hti:htiList) {
//				String htaskId = hti.getId();
//				List<Comment> taskList = taskService.getTaskComments(htaskId);
//				list.addAll(taskList);
//			}
//		}
		list = taskService.getProcessInstanceComments(processInstanceId);

		return list;
	}

	@Override
	public List<Comment> findCommentByLeaveBillId(Long id) {
		
		LeaveBill leaveBill = leaveBillDao.findLeaveBillById(id);
		String objectName = leaveBill.getClass().getSimpleName();
		String objId = objectName + "." + id;
		
//		HistoricProcessInstance hpi =  historyService.createHistoricProcessInstanceQuery()
//				.processInstanceBusinessKey(objId)
//				.singleResult();
//		
//		String processInstanceId = hpi.getId();
		
		HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery()
			.variableValueEquals("objId", objId)
			.singleResult();
		String processInstanceId = hvi.getProcessInstanceId();
		
		List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
				
		return list;
	}

	@Override
	public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
		Task task =  taskService.createTaskQuery()
				.taskId(taskId)
				.singleResult();		
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
			.processDefinitionId(processDefinitionId)
			.singleResult();
		
		return pd;
	}

	@Override
	public Map<String, Object> findCoordingByTaskId(String taskId) {
		Map<String, Object> map = new HashMap<String,Object>();
		Task task =  taskService.createTaskQuery()
				.taskId(taskId)
				.singleResult();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
		
		String processInstanceId = task.getProcessInstanceId();
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()
			.processInstanceId(processInstanceId).singleResult();
		
		String activityId = pi.getActivityId();
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}
	
	
	
}
