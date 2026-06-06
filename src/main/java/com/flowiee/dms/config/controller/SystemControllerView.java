package com.flowiee.dms.config.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.config.service.ConfigService;
import com.flowiee.dms.common.utils.PagesUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemControllerView extends BaseController {
    ConfigService configService;

    @GetMapping("/notification")
    public ModelAndView getAllNotification() {
        return baseView(new ModelAndView(PagesUtils.SYS_NOTIFICATION));
    }

    @GetMapping("/log")
    @PreAuthorize("@vldModuleSystem.readLog(true)")
    public ModelAndView showPageLog() {
        return baseView(new ModelAndView(PagesUtils.SYS_LOG));
    }

    @GetMapping("/config")
    @PreAuthorize("@vldModuleSystem.setupConfig(true)")
    public ModelAndView showConfig() {
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_CONFIG);
        modelAndView.addObject("listConfig", configService.findAll());
        return baseView(modelAndView);
    }

    @PostMapping("/config/update/{id}")
    @PreAuthorize("@vldModuleSystem.setupConfig(true)")
    public ModelAndView update(@ModelAttribute("config") SystemConfig config, @PathVariable("id") Long configId) {
        if (configId <= 0 || configService.findById(configId).isEmpty()) {
            throw new ResourceNotFoundException("Config not found!", true);
        }
        configService.update(config, configId);
        return new ModelAndView("redirect:/he-thong/config");
    }
}