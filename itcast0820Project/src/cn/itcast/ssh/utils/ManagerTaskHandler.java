package cn.itcast.ssh.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.catalina.core.ApplicationContext;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.sun.glass.ui.Application;

import cn.itcast.ssh.domain.Employee;
import cn.itcast.ssh.service.IEmployeeService;

/**
 * 员工经理任务分配
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
//		Employee employee = SessionContext.get();
//		delegateTask.setAssignee(employee.getManager().getName());
		Employee employee = SessionContext.get();
		String name = employee.getName();
		//ApplicationContext ac = new ClassPathXmlApplicationContext("application.xml")
		WebApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext.getServletContext());
		IEmployeeService employeeService = (IEmployeeService)ac.getBean("employeeService");
		Employee emp = employeeService.findEmployeeByName(name);
		delegateTask.setAssignee(emp.getManager().getName());
	}

}
