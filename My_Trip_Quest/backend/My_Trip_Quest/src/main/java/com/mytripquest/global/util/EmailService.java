package com.mytripquest.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendVerificationEmail(String to, String subject, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);

            String htmlContent = "<!DOCTYPE html>"
                               + "<html lang='ko'>"
                               + "<head>"
                               + "    <meta charset='UTF-8'>"
                               + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                               + "    <title>이메일 인증 코드</title>"
                               + "    <style>"
                               + "        body { font-family: 'Apple SD Gothic Neo', 'Malgun Gothic', '맑은 고딕', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                               + "        .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }"
                               + "        .header { text-align: center; padding-bottom: 20px; border-bottom: 1px solid #eeeeee; }"
                               + "        .header h1 { font-size: 24px; color: #333333; margin: 0; }"
                               + "        .content { padding: 20px 0; text-align: center; }"
                               + "        .content p { font-size: 16px; color: #555555; line-height: 1.5; }"
                               + "        .code-box { background-color: #e0f7fa; border: 1px solid #00bcd4; border-radius: 4px; padding: 15px 20px; display: inline-block; margin: 20px 0; }"
                               + "        .code-box strong { font-size: 28px; color: #00838f; letter-spacing: 2px; }"
                               + "        .footer { text-align: center; padding-top: 20px; border-top: 1px solid #eeeeee; font-size: 14px; color: #888888; }"
                               + "    </style>"
                               + "</head>"
                               + "<body>"
                               + "    <div class='container'>"
                               + "        <div class='header'>"
                               + "            <h1>My Trip Quest 이메일 인증</h1>"
                               + "        </div>"
                               + "        <div class='content'>"
                               + "            <p>안녕하세요! My Trip Quest에 가입해 주셔서 감사합니다.</p>"
                               + "            <p>아래 인증 코드를 회원가입 화면에 입력하여 이메일 인증을 완료해 주세요.</p>"
                               + "            <div class='code-box'>"
                               + "                <strong>" + code + "</strong>"
                               + "            </div>"
                               + "            <p>본 코드는 5분간 유효합니다. 코드를 요청하지 않으셨다면, 이 이메일을 무시하셔도 됩니다.</p>"
                               + "        </div>"
                               + "        <div class='footer'>"
                               + "            <p>&copy; 2025 My Trip Quest. All rights reserved.</p>"
                               + "        </div>"
                               + "    </div>"
                               + "</body>"
                               + "</html>";

            message.setText(htmlContent, "UTF-8", "html");
            javaMailSender.send(message);
            log.info("Successfully sent verification email to {}", to);
        } catch (MailException e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
            // TODO: 메일 전송 실패 시 적절한 에러 처리 (예: BusinessException 발생)
            // 현재는 시스템 에러로 간주하고 로그만 남김.
        } catch (jakarta.mail.MessagingException e) {
            log.error("Failed to create MimeMessage for {}: {}", to, e.getMessage());
        }
    }
}
