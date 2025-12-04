package org.example.expert.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AdminCheckAop {

    // 어떤 것 : CommentAdminController 클래스의 deleteComment()와 UserAdminController 클래스의 changeUserRole()
    // 언제 : 메서드 실행 전후에
    // 어떻게 : 접근 로그를 기록(요청한 사용자의 ID, API 요청 시각, API 요청 URL, 요청 본문, 응답 본문)

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..))")
    public Object commentApproachAdmin(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();


        Map<String, Object> params = new HashMap<>();

        // 메서드 실행 전
        String requestUserId = String.valueOf(request.getAttribute("userId"));
        long start = System.currentTimeMillis();

        try {
            String decodedRequestURI = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);
            params.put("requestUserId", requestUserId);
            params.put("start", start);
            params.put("requestURL", decodedRequestURI);
        } catch (Exception e) {
            log.error("LogAspect Error", e);
        }

        log.info("Request - User: {} | Time: {} | URI: {}",
            params.get("requestUserId"),
            params.get("start"),
            params.get("requestURL"));


        Object result = joinPoint.proceed();

        // 메서드 실행 후
        return result;
    }

    @Around("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public Object UserApproachAdmin(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request =  ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
        Map<String, Object> params = new HashMap<>();

        // 메서드 실행 전
        String requestUserId = String.valueOf(request.getAttribute("userId"));
        long start = System.currentTimeMillis();
        String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

        try {
            String decodedRequestURI = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);
            params.put("requestUserId", requestUserId);
            params.put("start", start);
            params.put("requestURL", decodedRequestURI);
            params.put("requestBody", requestBody);
        } catch (Exception e) {
            log.error("LogAspect Error", e);
        }

        Object result = joinPoint.proceed();

        log.info("Request - User: {} | Time: {} | URI: {} | requestBody: {} | responseBody: {}",
                params.get("requestUserId"),
                params.get("start"),
                params.get("requestURL"),
                params.get("requestBody"),
                result);

        // 메서드 실행 후
        return result;
    }
}
