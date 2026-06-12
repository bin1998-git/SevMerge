package com.example.SevMerge.notification;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.notification.SolApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SolApiService solApiService;
    private final HttpSession session;

    // 인증번호 발송
    public void sendSms(String phone) {
        // 하이픈 제거 (SolApi는 숫자만)
        String to = phone.replaceAll("-", "");

        // 6자리 인증번호 생성
        String code = String.valueOf(new Random().nextInt(900000) + 100000);

        // 세션에 (전화번호 + 인증번호) 저장 (검증용)
        session.removeAttribute("sms_phone");
        session.setAttribute("sms_phone", to);
        session.removeAttribute("sms_code");
        session.setAttribute("sms_code", code);


        log.info("SMS 인증번호 생성 - phone={}, code={}", to, code);

        // 실제 발송 //테스트비용아끼기
        //solApiService.sendSms(to, "[Sev Merge] 인증번호는 [" + code + "] 입니다.");
    }

    // 인증번호 검증
    public boolean checkSms(String phone, String code) {
        String to = phone.replaceAll("-", "");
        String savedPhone = (String) session.getAttribute("sms_phone");
        String savedCode  = (String) session.getAttribute("sms_code");

        log.info("SMS 검증 - 입력폰={}, 저장폰={}, 입력코드={}, 저장코드={}", to, savedPhone, code, savedCode);
        if (savedPhone == null || savedCode == null) return false;
        if (!savedPhone.equals(to)) return false;

        boolean ok = savedCode.equals(code);
        if (ok) {
            // 인증 성공 표시 (가입 시 확인용)
            session.setAttribute("verified_phone", to);
            session.removeAttribute("sms_code");
            session.removeAttribute("sms_phone");
            log.info("SMS 인증 성공 - phone={}", to);
        }
        return ok;
    }
}