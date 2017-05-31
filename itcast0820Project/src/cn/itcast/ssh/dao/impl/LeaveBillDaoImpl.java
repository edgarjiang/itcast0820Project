package cn.itcast.ssh.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.itcast.ssh.dao.ILeaveBillDao;
import cn.itcast.ssh.domain.Employee;
import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.utils.SessionContext;

public class LeaveBillDaoImpl extends HibernateDaoSupport implements ILeaveBillDao {

	@Override
	public List<LeaveBill> findLeaveBillList() {
		Employee employee = SessionContext.get();
		String hql = "from LeaveBill o where o.user=?";
		List<LeaveBill> list = (List<LeaveBill>) this.getHibernateTemplate().find(hql, employee);
		return list;
	}

	@Override
	public void saveLeaveBill(LeaveBill leaveBill) {
		this.getHibernateTemplate().save(leaveBill);
		
	}

	@Override
	public LeaveBill findLeaveBillById(Long id) {
		return this.getHibernateTemplate().get(LeaveBill.class, id);
	}

	@Override
	public void updateLeaveBill(LeaveBill leaveBill) {
		this.getHibernateTemplate().update(leaveBill);
	}

	@Override
	public void deleteLeaveBillById(Long id) {
		LeaveBill leaveBill = findLeaveBillById(id);
		this.getHibernateTemplate().delete(leaveBill);
	}	
}
