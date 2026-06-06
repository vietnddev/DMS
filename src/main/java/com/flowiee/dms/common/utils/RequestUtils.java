package com.flowiee.dms.common.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;

public class RequestUtils {
    public static String getRequestParam(ServletRequestAttributes attributes) {
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> parameterNames = request.getParameterNames();
            StringBuilder params = new StringBuilder();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                params.append(paramName).append("=").append(paramValue).append(", ");
            }
            String paramsStr = params.toString();
            if (paramsStr.endsWith(", ")) {
                paramsStr = paramsStr.substring(0, paramsStr.length() - 2);
            }
            return paramsStr;
        }
        return "";
    }

    public static String getRequestBody(JoinPoint joinPoint) {
        // Lấy ra chữ ký của phương thức (MethodSignature)
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // Lấy danh sách tham số của phương thức
        Object[] args = joinPoint.getArgs();
        // Lấy danh sách các annotation của từng tham số
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        // Loop qua các tham số và kiểm tra xem có annotation @RequestBody không
        for (int i = 0; i < args.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof RequestBody) {
                    return args[i].toString();
                }
            }
        }
        return "";
    }

    public static String getHttpMethod(ServletRequestAttributes attributes) {
        if (attributes != null) {
            return attributes.getRequest().getMethod();
        }
        return "";
    }

    public static String getRequestUrl(ServletRequestAttributes attributes) {
        if (attributes != null) {
            HttpServletRequest httpServletRequest = attributes.getRequest();
            return httpServletRequest.getRequestURL().toString();
        }
        return "";
    }

    public static boolean isLoginPage(ServletRequestAttributes attributes) {
        return getRequestUrl(attributes).contains(AppConstants.EP_SYS_LOGIN);
    }

    public static String getProcessClass(JoinPoint pJoinPoint) {
        Signature lvSignature = pJoinPoint.getSignature();
        return CoreUtils.trim(lvSignature.getDeclaringTypeName());
    }

    public static String getProcessMethod(JoinPoint pJoinPoint) {
        Signature lvSignature = pJoinPoint.getSignature();
        return CoreUtils.trim(lvSignature.getName());
    }

    public static HttpHeaders createHttpHeader(String pResponseFileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pResponseFileName);
        return httpHeaders;
    }
}