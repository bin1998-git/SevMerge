package com.example.SevMerge.member;

import com.example.SevMerge.core.exception.BadRequestException;
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

}
