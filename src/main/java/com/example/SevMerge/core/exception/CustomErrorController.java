package com.example.SevMerge.core.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * [L2] Custom error controller that replaces Spring Boot's default /error endpoint.
 *
 * The default Whitelabel error page exposes:
 *   - Spring Boot version
 *   - Full stack traces
 *   - Internal exception messages
 *   - Timestamp / path / status strings that aid fingerprinting
 *
 * This controller returns a minimal, safe error response instead.
 */
@Slf4j
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error", produces = {MediaType.TEXT_HTML_VALUE, MediaType.ALL_VALUE})
    public String handleHtmlError(HttpServletRequest request, Model model) {
        int status = getStatus(request);
        log.warn("[L2] Error endpoint called - status={}, path={}",
                status, request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        model.addAttribute("msg", getGenericMessage(status));

        if (status == 404) {
            return "err/404";
        }
        return "err/500";
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleJsonError(HttpServletRequest request) {
        int status = getStatus(request);
        log.warn("[L2] Error endpoint called (JSON) - status={}, path={}",
                status, request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        Map<String, Object> body = Map.of(
                "status", status,
                "error", HttpStatus.resolve(status) != null
                        ? HttpStatus.resolve(status).getReasonPhrase()
                        : "Error",
                "message", getGenericMessage(status)
        );
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private int getStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) return code;
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private String getGenericMessage(int status) {
        return switch (status) {
            case 400 -> "잘못된 요청입니다.";
            case 401 -> "인증이 필요합니다.";
            case 403 -> "접근 권한이 없습니다.";
            case 404 -> "요청하신 페이지를 찾을 수 없습니다.";
            case 405 -> "허용되지 않는 요청 방식입니다.";
            case 429 -> "너무 많은 요청입니다. 잠시 후 다시 시도해주세요.";
            default  -> "서비스 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        };
    }
}
