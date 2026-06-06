package com.flowiee.dms.storage.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.storage.model.DashboardModel;
import com.flowiee.dms.storage.service.DashboardService;
import com.flowiee.dms.common.utils.PagesUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class HomeController extends BaseController {
    DashboardService dashboardService;

    @GetMapping
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public ModelAndView dashboard() {
        DashboardModel dashboardModel = dashboardService.loadDashboard();
        ModelAndView modelAndView = new ModelAndView(PagesUtils.STG_DASHBOARD);
        modelAndView.addObject("dashboardModel", dashboardModel);
        return baseView(modelAndView);
    }
}