package com.example.SevMerge.notification;

import com.example.SevMerge.core.exception.UnauthorizedException;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolApiService {

    private final DefaultMessageService messageService;

    @Value("${solapi.sender-number}")
    private String senderNumber;

    // 메세지 전송 메서드
    public void sendSms(String to, String text) {
        Message msg = new Message();
        msg.setFrom(senderNumber);
        msg.setTo(to);
        msg.setText(text);

        try {
            messageService.send(msg);
            log.info("문자 발송 성공 - 수신자 : {}", to);
        } catch (SolapiMessageNotReceivedException e) {
            throw new UnauthorizedException("메세지 발송 실패 : " + e.getMessage());
        } catch (SolapiEmptyResponseException e) {
            throw new UnauthorizedException("메세지 내용 소실 : " + e.getMessage());
        } catch (SolapiUnknownException e) {
            throw new UnauthorizedException("예기치 못한 오류 발생 : " + e.getMessage());
        }
    }




}
