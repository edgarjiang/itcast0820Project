package cn.itcast.ssh.service.impl;

import java.util.List;

import cn.itcast.ssh.dao.ILeaveBillDao;
import cn.itcast.ssh.domain.Employee;
import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.service.ILeaveBillService;
import cn.itcast.ssh.utils.SessionContext;

public class LeaveBillServiceImpl implements ILeaveBillService {

	private ILeaveBillDao leaveBillDao;

	public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
		this.leaveBillDao = leaveBillDao;
	}

	@Override
	public List<LeaveBill> findLeaveBillList() {
		List<LeaveBill> list = leaveBillDao.findLeaveBillList();
		return list;
	}

	@Override
	public void saveLeaveBill(LeaveBill leaveBill) {
		Long id = leaveBill.getId();
		if (id == null) {
			leaveBill.setUser(SessionContext.get());
			leaveBillDao.saveLeaveBill(leaveBill);
		}
		else {
			leaveBillDao.updateLeaveBill(leaveBill);
		}
	}

	@Override
	public LeaveBill findLeaveBillById(Long id) {
		LeaveBill bill = leaveBillDao.findLeaveBillById(id);
		return bill;
	}

	@Override
	public void deleteLeaveBillById(Long id) {
		leaveBillDao.deleteLeaveBillById(id);
		
	}
}
