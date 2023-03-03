package de.viadee.pabbackend.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class MailService {


  private final JavaMailSender javaMailSender;

  @Value("${spring.mail.address}")
  private String mailAddress;

  public MailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  /**
   * Versendet eine Mail von Systemkonto an die übergebene Adressen.
   *
   * @param to      Die Zieladressen
   * @param subject Der Betreff
   * @param msg     Die Nachricht
   */
  public void sendMail(final String[] to, final String subject, final String msg) {

    final MimeMessage message = javaMailSender.createMimeMessage();
    try {
      final MimeMessageHelper helper = new MimeMessageHelper(message, false);
      helper.setFrom(mailAddress);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(msg);

      javaMailSender.send(message);
    } catch (final MessagingException e) {
    }

  }

  /**
   * Versendet eine Mail mit Anhaengen von Systemkonto an die übergebenen Adressen.
   *
   * @param to
   * @param subject
   * @param msg
   * @param anhaenge Beliebig viele Paare von Anhangnamen und AnhangByteArray
   */
  public void sendMailWithAttachments(final String[] to, final String subject, final String msg,
      final List<Pair<String, byte[]>> anhaenge) throws MessagingException {
    final MimeMessage message = javaMailSender.createMimeMessage();

    final MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setFrom(mailAddress);
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(msg);

    for (final Pair<String, byte[]> anhang : anhaenge) {
      helper.addAttachment(anhang.getLeft(), new ByteArrayResource(anhang.getRight()));
    }
    javaMailSender.send(message);

  }


}
