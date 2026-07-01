package com.example.SevMerge.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
                    location.href='/login';
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

    @ExceptionHandler(AdminException.class)
    @ResponseBody
    public String adminException(AdminException e, HttpServletRequest request) {
        log.warn("=== AdminException ===");
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

    @ExceptionHandler(CalculateException.class)
    @ResponseBody
    public String calculateException(CalculateException e, HttpServletRequest request) {
        log.warn("=== CalculateException ===");
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
    public Object runtime(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn("=== 예상치 못한 RuntimeException ===");
        log.warn("요청 URL : {}", request.getRequestURL());
        log.warn("에러 메시지 : {}", e.getMessage());

        // SSE 엔드포인트(/notifications/subscribe)에서 예외 발생 시
        // text/event-stream 요청에 HTML 뷰를 돌려주면 브라우저 EventSource가
        // 잘못된 응답을 받아 무한 재연결을 시도하거나 "이상한 도메인" 표시가 생길 수 있음.
        // Accept: text/event-stream 요청이면 빈 SSE 스트림으로 조용히 종료.
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            try {
                response.getWriter().write("");
            } catch (Exception ignored) {}
            return null;
        }

        request.setAttribute("msg", "시스템 오류가 발생했습니다. 관리자에게 문의해주세요.");
        return "err/500";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handelMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("지원하지 않는 HTTP 메서드 요청: {}", request.getRequestURI());
        return "redirect:/";
    }

    @ExceptionHandler(FileException.class)
    @ResponseBody
    public String fileException(FileException e, HttpServletRequest request) {
        log.warn("=== File 전송 오류 ===");
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

}
