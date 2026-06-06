package com.flowiee.dms.common.web;

import com.flowiee.dms.account.service.AccountService;
import com.flowiee.dms.common.service.BaseAuthorize;
import com.flowiee.dms.common.utils.AppConstants;
import com.flowiee.dms.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class BaseController extends BaseAuthorize {
	@Autowired protected AccountService accountService;

	protected ModelAndView baseView(ModelAndView modelAndView) {
		modelAndView.addObject("USERNAME_LOGIN", SecurityUtils.getCurrentUser().getUsername());
		setURLHeader(modelAndView);
		setURLSidebar(modelAndView);
		return modelAndView;
	}

	private void setURLHeader(ModelAndView modelAndView) {
		modelAndView.addObject("URL_PROFILE", AppConstants.EP_SYS_PROFILE);
		modelAndView.addObject("URL_LOGOUT", AppConstants.EP_SYS_LOGOUT);
	}
	
	private void setURLSidebar(ModelAndView modelAndView) {
		modelAndView.addObject("URL_CATEGORY", AppConstants.EP_CATEGORY);
		modelAndView.addObject("URL_STORAGE_DASHBOARD", AppConstants.EP_STORAGE);
		modelAndView.addObject("URL_STORAGE_DOCUMENT", AppConstants.EP_STORAGE_DOCUMENT);
		modelAndView.addObject("URL_SYSTEM_CONFIG", AppConstants.EP_SYS_CONFIG);
		modelAndView.addObject("URL_SYSTEM_LOG", AppConstants.EP_SYS_LOG);
		modelAndView.addObject("URL_SYS_ACCOUNT", AppConstants.EP_SYS_ACCOUNT);
		modelAndView.addObject("URL_SYS_GR_ACCOUNT", AppConstants.EP_URL_SYS_GR_ACCOUNT);
	}
}