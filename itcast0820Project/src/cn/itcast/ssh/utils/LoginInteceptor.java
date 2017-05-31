package cn.itcast.ssh.utils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import cn.itcast.ssh.domain.Employee;


/**
 * 登录验证拦截器
 *
 */
@SuppressWarnings("serial")
public class LoginInteceptor implements Interceptor {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		String actionName = invocation.getProxy().getActionName();
		if (!"loginAction_login".equals(actionName)) {
			Employee employee = SessionContext.get();
			if (null == employee) {
				return "login";
			}
		}
		return invocation.invoke();
		
	}

}
