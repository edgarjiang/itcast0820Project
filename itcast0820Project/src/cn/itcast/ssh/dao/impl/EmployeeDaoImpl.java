package cn.itcast.ssh.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.itcast.ssh.dao.IEmployeeDao;
import cn.itcast.ssh.domain.Employee;

public class EmployeeDaoImpl extends HibernateDaoSupport implements IEmployeeDao {

	@Override
	public Employee findEmployeeByName(String name) {
		String hql = "from Employee o where o.name = ?";
		List<Employee> list = (List<Employee>) this.getHibernateTemplate().find(hql, name);
		Employee employee = null;
		if (list != null && list.size() > 0) {
			employee = list.get(0);
		}
		return employee;
	}
	
	
}
