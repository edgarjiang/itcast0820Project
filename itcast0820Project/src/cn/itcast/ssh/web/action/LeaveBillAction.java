package cn.itcast.ssh.web.action;

import cn.itcast.ssh.domain.LeaveBill;
import cn.itcast.ssh.service.ILeaveBillService;
import cn.itcast.ssh.utils.ValueContext;

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@SuppressWarnings("serial")
public class LeaveBillAction extends ActionSupport implements ModelDriven<LeaveBill> {

	private LeaveBill leaveBill = new LeaveBill();
	
	@Override
	public LeaveBill getModel() {
		return leaveBill;
	}
	
	private ILeaveBillService leaveBillService;

	public void setLeaveBillService(ILeaveBillService leaveBillService) {
		this.leaveBillService = leaveBillService;
	}

	/**
	 * 请假管理首页显示
	 * @return
	 */
	public String home(){
		List<LeaveBill> list = leaveBillService.findLeaveBillList();
		ValueContext.putValueContext("list", list);
		return "home";
	}
	
	/**
	 * 添加请假申请
	 * @return
	 */
	public String input(){
		Long id = leaveBill.getId();
		if (id != null) {
			LeaveBill bill = leaveBillService.findLeaveBillById(id);
			ValueContext.putValueStack(bill);
		}
		return "input";
	}
	
	/**
	 * 保存/更新，请假申请
	 * 
	 * */
	public String save() {
		leaveBillService.saveLeaveBill(leaveBill);
		return "save";
	}
	
	/**
	 * 删除，请假申请
	 * 
	 * */
	public String delete(){
		Long id = leaveBill.getId();
		leaveBillService.deleteLeaveBillById(id);
		return "save";
	}
	
}
