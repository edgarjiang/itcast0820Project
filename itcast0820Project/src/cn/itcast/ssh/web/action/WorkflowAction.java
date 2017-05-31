package cn.itcast.ssh.web.action;

import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.service.ILeaveBillService;
import cn.itcast.ssh.service.IWorkflowService;
import cn.itcast.ssh.utils.SessionContext;
import cn.itcast.ssh.utils.ValueContext;
import cn.itcast.ssh.web.form.WorkflowBean;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@SuppressWarnings("serial")
public class WorkflowAction extends ActionSupport implements ModelDriven<WorkflowBean> {

	private WorkflowBean workflowBean = new WorkflowBean();
	
	@Override
	public WorkflowBean getModel() {
		return workflowBean;
	}
	
	private IWorkflowService workflowService;
	
	private ILeaveBillService leaveBillService;

	public void setLeaveBillService(ILeaveBillService leaveBillService) {
		this.leaveBillService = leaveBillService;
	}

	public void setWorkflowService(IWorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	/**
	 * 部署管理首页显示
	 * @return
	 */
	public String deployHome(){
		List<Deployment> depList = workflowService.findDeploymentList();
		List<ProcessDefinition> pdList = workflowService.findProcessDefinitionList();
		
		ValueContext.putValueContext("depList", depList);
		ValueContext.putValueContext("pdList", pdList);
		return "deployHome";
	}
	
	/**
	 * 发布流程
	 * @return
	 */
	public String newdeploy(){
		File file = workflowBean.getFile();
		String filename = workflowBean.getFilename();
		workflowService.saveNewDeploye(file, filename);
		return "list";
	}
	
	/**
	 * 删除部署信息
	 */
	public String delDeployment(){
		String deploymentId = workflowBean.getDeploymentId();
		workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
		return "list";
	}
	
	/**
	 * 查看流程图
	 */
	public String viewImage() throws Exception {
		String deploymentId = workflowBean.getDeploymentId();
		String imageName = workflowBean.getImageName();
		InputStream in = workflowService.findImageInputStream(deploymentId, imageName);
		OutputStream out =  ServletActionContext.getResponse().getOutputStream();
		for (int b=-1; (b=in.read()) != -1;) {
			out.write(b);
		}
		out.close();
		in.close();
		return null;
	}
	
	// 启动流程
	public String startProcess(){
		workflowService.saveStartProcess(workflowBean);
		return "listTask";
	}
	
	
	
	/**
	 * 任务管理首页显示
	 * @return
	 */
	public String listTask(){
		String name = SessionContext.get().getName();
		List<Task> list = workflowService.findTaskListByName(name);
		ValueContext.putValueContext("list", list);
		return "task";
	}
	
	/**
	 * 打开任务表单
	 */
	public String viewTaskForm(){
		String taskId = workflowBean.getTaskId();
		String url =  workflowService.findTaskFormKeyByTaskId(taskId);
		url += "?taskId=" + taskId;
		ValueContext.putValueContext("url", url);
		
		return "viewTaskForm";
	}
	
	// 准备表单数据
	public String audit(){
		String taskId = workflowBean.getTaskId();
		LeaveBill leaveBill = workflowService.findLeaveBillByTaskId(taskId);
		ValueContext.putValueStack(leaveBill);
		
		List<String> outcomeList = workflowService.findOutcomeListByTaskId(taskId);
		ValueContext.putValueContext("outcomeList", outcomeList);
		
		List<Comment> commentList = null;
		ValueContext.putValueContext("commentList", commentList);
		
		return "taskForm";
	}
	
	
	/**
	 * 提交任务
	 */
	public String submitTask(){
		return "listTask";
	}
	
	/**
	 * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
	 */
	public String viewCurrentImage(){
		return "image";
	}
	
	// 查看历史的批注信息
	public String viewHisComment(){
		return "viewHisComment";
	}
}
