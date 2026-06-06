package com.flowiee.dms.common.web;

import com.flowiee.dms.audit.entity.EventLog;
import com.flowiee.dms.audit.repository.EventLogRepository;
import com.flowiee.dms.audit.service.EventLogService;
import com.flowiee.dms.common.utils.RequestUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RestControllerAspect {
    private final EventLogRepository mvEventLogRepository;
    private final EventLogService mvEventLogService;

    private ThreadLocal<RequestContext> mvRequestContext = ThreadLocal.withInitial(RequestContext::new); // Tạo ThreadLocal để lưu thông tin của request

    @Getter
    @Setter
    public static class RequestContext {
        private long requestId;
        private long startTime;
        private String username;
        private String ip;
    }

    @Around("execution(* com.flowiee.dms.controller.*.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String lvProcessMethod = RequestUtils.getProcessMethod(joinPoint);
        String lvUri = attributes.getRequest().getRequestURI();
        if (!lvUri.contains("/api/v1") || "handleFileRequest".equals(lvProcessMethod)) {
            return joinPoint.proceed();
        }
        LocalDateTime lvRequestTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault());

        EventLog eventLog = mvEventLogService.writeLog(attributes, joinPoint, lvRequestTime, "DMS");
        long lvRequestId = eventLog.getRequestId();
        String lvUsername = eventLog.getCreatedBy();
        String lvIpAddress = eventLog.getIpAddress();

        RequestContext lvRequestContext = mvRequestContext.get();
        lvRequestContext.setRequestId(lvRequestId);
        lvRequestContext.setStartTime(startTime);
        lvRequestContext.setUsername(lvUsername);
        lvRequestContext.setIp(lvIpAddress);
        mvRequestContext.set(lvRequestContext);

        MDC.put("customKey", String.valueOf(lvRequestContext.getRequestId()));

        log.info("[REQUEST {}] {}",
                lvRequestContext.getRequestId(),
                formatRequestInfo(attributes.getRequest(), joinPoint, attributes, lvUsername));

        Object result;
        try {
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            eventLog.setDuration(duration);
            mvEventLogRepository.save(eventLog);

            log.info("[RESPONSE {}] ({} ms) {}",
                    lvRequestContext.getRequestId(),
                    duration,
                    result.toString());
            return result;
        } catch (Throwable ex) {
            log.error("[EXCEPTION {}] {}",
                    lvRequestContext.getRequestId(),
                    formatRequestException(attributes.getRequest(), joinPoint, ex.getMessage()),
                    ex);
            throw ex;
        } finally {
            MDC.remove("customKey");
            mvRequestContext.remove();
        }
    }

    public String formatRequestException(HttpServletRequest pRequest, JoinPoint pJoinPoint, String pMessage) {
        String lvHttpMethod = pRequest != null ? pRequest.getMethod() : "N/A";
        String lvUri = pRequest != null ? pRequest.getRequestURI() : "N/A";
        String lvClassName = pJoinPoint.getTarget().getClass().getSimpleName();
        String lvProcessMethod = RequestUtils.getProcessMethod(pJoinPoint);

        return String.format("%s %s - %s.%s with error: %s", lvHttpMethod, lvUri, lvClassName, lvProcessMethod, pMessage);
    }

    public String formatRequestInfo(HttpServletRequest pRequest, JoinPoint pJoinPoint, ServletRequestAttributes pAttributes, String pUser) {
        String lvHttpMethod = pRequest != null ? pRequest.getMethod() : "N/A";
        String lvUri = pRequest != null ? pRequest.getRequestURI() : "N/A";
        String lvClassName = pJoinPoint.getTarget().getClass().getSimpleName();
        String lvProcessMethod = RequestUtils.getProcessMethod(pJoinPoint);
        String lvRequestParam = RequestUtils.getRequestParam(pAttributes);
        String lvRequestBody = RequestUtils.getRequestBody(pJoinPoint);

        return String.format("%s %s - %s - %s.%s with params: [%s] & body: [%s]", lvHttpMethod, lvUri, pUser, lvClassName, lvProcessMethod, lvRequestParam, lvRequestBody);
    }
}