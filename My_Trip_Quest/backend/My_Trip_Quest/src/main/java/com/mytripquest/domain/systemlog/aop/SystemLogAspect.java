package com.mytripquest.domain.systemlog.aop;

import com.mytripquest.domain.systemlog.dto.SystemLogDto;
import com.mytripquest.domain.systemlog.service.SystemLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SystemLogAspect {

    private final SystemLogService systemLogService;
    private final com.mytripquest.domain.user.repository.UserMapper userMapper;

    // 모든 컨트롤러 패키지 내의 메서드를 대상으로 함
    @Pointcut("execution(* com.mytripquest.controller..*Controller.*(..))")
    public void controllerMethods() {
    }

    @Around("controllerMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String featureName = joinPoint.getSignature().getDeclaringType().getSimpleName() + "."
                + joinPoint.getSignature().getName();
        String status = "SUCCESS";
        String errorCode = null;
        String logMessage = null;

        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            status = "ERROR";
            errorCode = e.getClass().getSimpleName();
            logMessage = e.getMessage();
            throw e; // 예외를 다시 던져서 클라이언트에게 전달
        } finally {
            long endTime = System.currentTimeMillis();
            int executionTime = (int) (endTime - startTime);

            try {
                // 사용자 정보 추출 (SecurityContext)
                Long userId = null;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    Object principal = authentication.getPrincipal();

                    if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                        String username = ((org.springframework.security.core.userdetails.UserDetails) principal)
                                .getUsername();
                        userId = userMapper.findIdByEmail(username).orElse(null);
                    }
                }

                // IP 주소 추출
                String userIp = null;
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes()).getRequest();
                if (request != null) {
                    userIp = request.getRemoteAddr();
                }

                SystemLogDto logDto = new SystemLogDto();

                // 인수에서 targetId (questId) 추출 (Long 타입 인자 검색)
                Object[] args = joinPoint.getArgs();
                if (args != null) {
                    for (Object arg : args) {
                        if (arg instanceof Long) {
                            logDto.setTargetId((Long) arg);
                            break; // 첫 번째 Long 인자를 ID로 가정
                        }
                    }
                }

                logDto.setFeatureName(featureName);
                logDto.setUserId(userId);
                logDto.setUserIp(userIp);
                logDto.setStatus(status);
                logDto.setErrorCode(errorCode);
                logDto.setExecutionTime(executionTime);
                logDto.setLogMessage(logMessage);

                // 비동기 서비스 호출
                systemLogService.recordLog(logDto);

            } catch (Exception e) {
                log.error("Failed to record system log", e);
            }
        }
    }
}
