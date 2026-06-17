package com.example.SevMerge.member;

import com.example.SevMerge.core.util.MailUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final HttpSession session;

    public void sendEmail(String email) {
        String code = MailUtil.generateRandomCode();
        log.info("이메일 인증번호 생성 - email={}, code={}", email, code);
        MimeMessage emailMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(emailMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[IcodeU] 회원가입 인증 번호 안내");
            helper.setText(buildVerificationHtml(code), true);
            javaMailSender.send(emailMessage);

            session.setAttribute("code_" + email, code);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // GitHub 스타일 카드형 인증 메일 HTML
    private String buildVerificationHtml(String code) {
        return """
                <div style="margin:0;padding:0;background-color:#f5f6fa;">
                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f5f6fa;padding:40px 0;font-family:'Apple SD Gothic Neo','Malgun Gothic',Arial,sans-serif;">
                    <tr>
                      <td align="center">
                        <table role="presentation" width="440" cellpadding="0" cellspacing="0" style="width:440px;max-width:90%%;background-color:#ffffff;border:1px solid #e2e8f0;border-radius:14px;overflow:hidden;">

                          <!-- 헤더 -->
                          <tr>
                            <td style="padding:28px 32px 20px 32px;border-bottom:1px solid #eef0f6;">
                              <span style="font-size:20px;font-weight:800;color:#2563eb;letter-spacing:-0.5px;">IcodeU</span>
                            </td>
                          </tr>

                          <!-- 본문 -->
                          <tr>
                            <td style="padding:32px;">
                              <h1 style="margin:0 0 12px 0;font-size:20px;font-weight:700;color:#1c1e26;">이메일 인증을 완료해 주세요</h1>
                              <p style="margin:0 0 28px 0;font-size:14px;line-height:1.7;color:#6b7280;">
                                아래 인증 번호를 입력하면 회원가입이 계속 진행됩니다.<br>
                              </p>

                              <!-- 인증 코드 박스 -->
                              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center" style="background-color:#eff6ff;border:1.5px solid #bfdbfe;border-radius:12px;padding:22px;">
                                    <div style="font-size:32px;font-weight:800;letter-spacing:8px;color:#1d4ed8;font-family:'JetBrains Mono',Consolas,monospace;">%s</div>
                                  </td>
                                </tr>
                              </table>

                              <p style="margin:28px 0 0 0;font-size:12px;line-height:1.7;color:#9ca3af;">
                                본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.
                              </p>
                            </td>
                          </tr>

                          <!-- 푸터 -->
                          <tr>
                            <td style="padding:18px 32px;background-color:#fafbfe;border-top:1px solid #eef0f6;">
                              <span style="font-size:11px;color:#c4c9d9;">© IcodeU — IT 외주 입찰 플랫폼</span>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>
                </div>
                """.formatted(code);
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
