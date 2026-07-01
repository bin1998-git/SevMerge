package com.example.SevMerge.partnership;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerShipMailService {

    private final JavaMailSender mailSender;

    public void sendPartnerShipMail(PartnerShipRequest request) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo("dkswnsgus88@naver.com");
            helper.setReplyTo(request.getEmail()); // 발송한 메일에 답장하면 다시 내게오지않고 되돌아가는기능
            String subject;
            String typeLabel;
            String typeColor;

            if (request.getPartnerShipType() == PartnerShipType.COMPANY) {
                subject = "[기업 제휴 문의] " + request.getCompanyName();
                typeLabel = "🏢 기업 제휴";
                typeColor = "#2563eb";
            } else if (request.getPartnerShipType() == PartnerShipType.EDUCATION) {
                subject = "[교육기관 제휴 문의] " + request.getCompanyName();
                typeLabel = "🎓 교육기관 제휴";
                typeColor = "#059669";
            } else {
                subject = "[마케팅 제휴 문의] " + request.getCompanyName();
                typeLabel = "📣 마케팅 제휴";
                typeColor = "#f59e0b";
            }
            helper.setSubject(subject);
            helper.setText(
                    "<div style='font-family:Arial, sans-serif; max-width:600px; margin:auto; padding:30px; border:1px solid #e0e0e0; border-radius:8px;'>" +
                            "  <h2 style='color:" + typeColor + ";'>📩 제휴 문의가 접수되었습니다.</h2>" +
                            "  <p style='color:" + typeColor + "; font-weight:bold; margin-bottom:16px;'>" + typeLabel + "</p>" +
                            "  <table style='width:100%; border-collapse:collapse;'>" +
                            "    <tr style='border-bottom:1px solid #e0e0e0;'>" +
                            "      <td style='padding:12px; color:#888; width:30%;'>회사명</td>" +
                            "      <td style='padding:12px;'>" + request.getCompanyName() + "</td>" +
                            "    </tr>" +
                            "    <tr style='border-bottom:1px solid #e0e0e0;'>" +
                            "      <td style='padding:12px; color:#888;'>담당자명</td>" +
                            "      <td style='padding:12px;'>" + request.getManagerName() + "</td>" +
                            "    </tr>" +
                            "    <tr style='border-bottom:1px solid #e0e0e0;'>" +
                            "      <td style='padding:12px; color:#888;'>문의자 메일</td>" +
                            "      <td style='padding:12px;'>" + request.getEmail() + "</td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding:12px; color:#888;'>문의 내용</td>" +
                            "      <td style='padding:12px;'>" + request.getContent() + "</td>" +
                            "    </tr>" +
                            "  </table>" +
                            "  <hr style='border:none; border-top:1px solid #e0e0e0; margin-top:20px;'/>" +
                            "  <p style='color:#888; font-size:12px;'>본 메일은 자동 발송된 메일입니다.</p>" +
                            "</div>"
                    , true);
            if (request.getPartnerFile() != null && !request.getPartnerFile().isEmpty()) {
                helper.addAttachment(
                        request.getPartnerFile().getOriginalFilename(),
                        request.getPartnerFile()
                );
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    // 승인시 해당 메일 자동 발송
    public void sendPartnerShipMailApprove(String email) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(email);
            helper.setReplyTo(email);
            helper.setSubject("[SevMerge] 제휴 신청이 승인되었습니다.");
            helper.setText(
                    "<div style='font-family:Arial, sans-serif; max-width:600px; margin:auto; padding:30px; border:1px solid #e0e0e0; border-radius:8px;'>" +
                            "  <h2 style='color:#4CAF50;'>✅ 제휴 신청 승인 안내</h2>" +
                            "  <p>안녕하세요,</p>" +
                            "  <p>귀하의 <strong>제휴 신청이 승인</strong>되었습니다.</p>" +
                            "  <p>앞으로 SevMerge 플랫폼을 통해 다양한 프로젝트에 참여하실 수 있습니다.</p>" +
                            "  <hr style='border:none; border-top:1px solid #e0e0e0;'/>" +
                            "  <p style='color:#888; font-size:12px;'>본 메일은 자동 발송된 메일입니다.</p>" +
                            "</div>", true
            );
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // 거절시 메일 자동발송
    public void sendPartnerShipMailReject(String email) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        try {
            helper.setTo(email);
            helper.setReplyTo(email);
            helper.setSubject("[SevMerge] 제휴 신청이 거절되었습니다.");
            helper.setText(
                    "<div style='font-family:Arial, sans-serif; max-width:600px; margin:auto; padding:30px; border:1px solid #e0e0e0; border-radius:8px;'>" +
                            "  <h2 style='color:#f44336;'>❌ 제휴 신청 거절 안내</h2>" +
                            "  <p>안녕하세요,</p>" +
                            "  <p>귀하의 <strong>제휴 신청이 거절</strong>되었습니다.</p>" +
                            "  <p>자세한 문의사항은 고객센터로 연락해 주시기 바랍니다.</p>" +
                            "  <hr style='border:none; border-top:1px solid #e0e0e0;'/>" +
                            "  <p style='color:#888; font-size:12px;'>본 메일은 자동 발송된 메일입니다.</p>" +
                            "</div>", true
            );
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
