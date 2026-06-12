package com.example.SevMerge.member;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.notification.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MailService mailService;
    private final SmsService smsService;

    // 메일 인증 번호 발송
    // 주소설계 : http://localhost:8080/api/email/send , POST
    @PostMapping("/api/email/send")
    public ResponseEntity<?> sendEmail(@RequestBody MemberRequest.EmailCheckDTO reqDTO) {
        reqDTO.validate();
        mailService.sendEmail(reqDTO.getEmail());
        return ResponseEntity.ok("인증 번호 발송 됨");

    }

    @PostMapping("/api/email/verify")
    public ResponseEntity<?> checkEmail(@RequestBody MemberRequest.EmailCheckDTO reqDTO) {
        reqDTO.validate();
        if (reqDTO.getCode() == null || reqDTO.getCode().isEmpty()) {
            throw new BadRequestException("인증번호를 입력해주세요.");
        }

        boolean isVerified = mailService.checkEmail(reqDTO.getEmail(), reqDTO.getCode());

        if (isVerified) {
            return ResponseEntity.ok().body(Map.of("message", "인증 되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 한습니다."));
        }

    }
    // 휴대폰 인증번호 발송
    @PostMapping("/api/sms/send")
    public ResponseEntity<?> sendSms(@RequestBody Map<String, String> req) {
        String phone = req.get("phone");
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException("휴대폰 번호를 입력해주세요.");
        }
        smsService.sendSms(phone);
        return ResponseEntity.ok("인증번호 발송됨");
    }

    // 휴대폰 인증번호 검증
    @PostMapping("/api/sms/verify")
    public ResponseEntity<?> checkSms(@RequestBody Map<String, String> req) {
        String phone = req.get("phone");
        String code  = req.get("code");
        if (code == null || code.isBlank()) {
            throw new BadRequestException("인증번호를 입력해주세요.");
        }
        boolean verified = smsService.checkSms(phone, code);
        if (verified) {
            return ResponseEntity.ok().body(Map.of("message", "인증되었습니다."));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다."));
    }

}
