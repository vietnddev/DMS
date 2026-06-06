package com.flowiee.dms.common.exception;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.common.utils.PagesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends BaseController {
    @ExceptionHandler
    public ModelAndView exceptionHandler(AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return new ModelAndView(PagesUtils.SYS_LOGIN);
    }

    @ExceptionHandler
    public Object exceptionHandler(ResourceNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        if (ex.isRedirectErrorUI()) {
            ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
            ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
            modelAndView.addObject("error", error);
            return baseView(modelAndView);
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.NOT_FOUND));
        }
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(BadRequestException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(DataExistsException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.CONFLICT));
    }

    @ExceptionHandler
    public ModelAndView exceptionHandler(ForbiddenException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        ModelAndView modelAndView = new ModelAndView(PagesUtils.SYS_ERROR);
        modelAndView.addObject("error", error);
        return baseView(modelAndView);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(DataInUseException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.LOCKED));
    }

    @ExceptionHandler
    public Object exceptionHandler(AccountLockedException ex) {
        log.error(ex.getMessage(), ex);
        if (ex.isRedirectView()) {
            ErrorResponse error = new ErrorResponse(HttpStatus.LOCKED.value(), ex.getMessage());
            ModelAndView modelAndView = new ModelAndView(ex.getView() != null ? ex.getView() : PagesUtils.SYS_ERROR);
            modelAndView.addObject("error", error);
            return baseView(modelAndView);
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.LOCKED));
        }
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(AppException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> exceptionHandler(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage(), ex, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}