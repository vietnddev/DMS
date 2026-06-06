package com.flowiee.dms.common.web;

import com.flowiee.dms.common.exception.ErrorResponse;
import com.flowiee.dms.common.utils.PagesUtils;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorControllerView extends BaseController {
    @GetMapping("/403")
    public ModelAndView forbiddenException() {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ErrorCode.FORBIDDEN_ERROR.getDescription());
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @GetMapping("/404")
    public ModelAndView notfoundException() {
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ErrorCode.NOTFOUND_ERROR.getDescription());
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }
}