package com.example.SevMerge.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public String badRequest(BadRequestException e, HttpServletRequest request) {
        log.warn("=== 400 Bad Request ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());
        String message = e.getMessage().replace("'", "\\'");
        return """
                <script>
                    alert('%s');
                    history.back();
                </script>
                """.formatted(message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public String unauthorized(UnauthorizedException e, HttpServletRequest request) {
        log.warn("=== 401 Unauthorized ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());
        String message = e.getMessage().replace("'", "\\'");
        return """
                <script>
                    alert('%s');
                    location.href='/login-form';
                </script>
                """.formatted(message);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    public String forbidden(ForbiddenException e, HttpServletRequest request) {
        log.warn("=== 403 Forbidden ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());



        String message = e.getMessage().replace("'", "\\'");



        return """
                <script>
                    alert('%s');
                    history.back();
                </script>
                """.formatted(message);
    }

    @ExceptionHandler(NotFoundException.class)
    public String notFound(NotFoundException e, HttpServletRequest request) {
        log.warn("=== 404 Not Found ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());
        request.setAttribute("msg", e.getMessage());
        return "err/404";
    }

    @ExceptionHandler(InternalServerException.class)
    public String internalServer(InternalServerException e, HttpServletRequest request) {
        log.warn("=== 500 Internal Server Error ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());
        request.setAttribute("msg", e.getMessage());
        return "err/500";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String dataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
        log.warn("=== DB 제약조건 위반 ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());
        if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY")) {
            request.setAttribute("msg", "관련된 데이터가 있어 삭제할 수 없습니다.");
        } else {
            request.setAttribute("msg", "데이터 처리 중 오류가 발생했습니다.");
        }
        return "err/500";
    }

    @ExceptionHandler(RuntimeException.class)
    public String runtime(RuntimeException e, HttpServletRequest request) {
        log.warn("=== 예상치 못한 RuntimeException ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());
        request.setAttribute("msg", "시스템 오류가 발생했습니다. 관리자에게 문의해주세요.");
        return "err/500";
    }
}
