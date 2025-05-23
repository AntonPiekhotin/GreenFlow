package org.greenflow.billing.util;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.greenflow.common.model.exception.GreenFlowException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.greenflow.common.model.constant.CustomHeaders.X_INTERNAL_TOKEN;

@Aspect
@Component
public class InternalAuthAspect {

    @Value("${api.internalApiToken}")
    private String expectedToken;

    @Around("@within(org.greenflow.common.util.InternalAuth) || @annotation(org.greenflow.common.util.InternalAuth)")
    public Object validateInternalToken(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new GreenFlowException(403, "No request context available");
        }

        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader(X_INTERNAL_TOKEN);

        if (!expectedToken.equals(token)) {
            throw new GreenFlowException(403, "Invalid or missing internal token");
        }

        return joinPoint.proceed();
    }
}