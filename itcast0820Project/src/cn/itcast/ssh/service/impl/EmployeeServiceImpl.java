package cn.itcast.ssh.service.impl;

import cn.itcast.ssh.dao.IEmployeeDao;
import cn.itcast.ssh.domain.Employee;
import cn.itcast.ssh.service.IEmployeeService;

public class EmployeeServiceImpl implements IEmployeeService {

	private IEmployeeDao employeeDao;

	public void setEmployeeDao(IEmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	/****/
	@Override
	public Employee findEmployeeByName(String name) {
		Employee emp = employeeDao.findEmployeeByName(name);
		return emp;
	}
	
	
}
