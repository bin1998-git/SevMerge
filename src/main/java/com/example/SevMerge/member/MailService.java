package com.example.SevMerge.member;

import com.example.SevMerge.core.util.MailUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final HttpSession session;

    public void sendEmail(String email) {
        String code = MailUtil.generateRandomCode();

        MimeMessage emailMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(emailMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[SevMerge 회원가입 인증 번호]"); // todo - 프로젝트명으로 수정
            helper.setText("<h3>인증 번호는 " + code + " 입니다<h3>", true);
            javaMailSender.send(emailMessage);

            session.setAttribute("code_" + email, code);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkEmail(String email, String code) {

        String savedCode = (String) session.getAttribute("code_" + email);

        if (savedCode != null && savedCode.equals(code)) {
            session.removeAttribute("code_" + email);

            session.setAttribute("verified_email", email);
            return true;
        }
        return false;
    }

}
