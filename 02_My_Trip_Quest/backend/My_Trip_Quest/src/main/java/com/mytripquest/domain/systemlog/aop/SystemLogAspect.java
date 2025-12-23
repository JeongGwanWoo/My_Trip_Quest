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
                if (authentication != null && authentication.isAuthenticated()
                        && !"anonymousUser".equals(authentication.getPrincipal())) {
                    // Principal이 String(email)인지, UserDetails인지 확인 필요.
                    // 여기서는 단순히 name(email)을 가져오거나, 커스텀 UserDetails에서 ID를 꺼낼 수 있음.
                    // 현재 프로젝트 구조상 userId를 직접 꺼내기 어려우면 null로 두거나,
                    // CustomUserDetails를 캐스팅해서 가져와야 함.
                    // 일단은 null로 두고, 필요 시 로직 추가.
                }

                // IP 주소 추출
                String userIp = null;
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes()).getRequest();
                if (request != null) {
                    userIp = request.getRemoteAddr();
                }

                SystemLogDto logDto = new SystemLogDto();

                // 인수에서 targetId (questId) 추출 (첫 번째 Long 인자가 questId라고 가정)
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0 && args[0] instanceof Long) {
                    logDto.setTargetId((Long) args[0]);
                }

                logDto.setFeatureName(featureName);
                logDto.setUserId(userId); // TODO: SecurityContext에서 ID 추출 로직 보완 필요
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
