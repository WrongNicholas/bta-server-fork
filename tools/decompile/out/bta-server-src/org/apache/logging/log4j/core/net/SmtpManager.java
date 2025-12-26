package org.apache.logging.log4j.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLSocketFactory;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.CyclicBuffer;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.util.PropertiesUtil;

public class SmtpManager extends MailManager {
   public static final SmtpManager.SMTPManagerFactory FACTORY = new SmtpManager.SMTPManagerFactory();
   private final Session session;
   private final CyclicBuffer<LogEvent> buffer;
   private volatile MimeMessage message;
   private final MailManager.FactoryData data;

   private static MimeMessage createMimeMessage(final MailManager.FactoryData data, final Session session, final LogEvent appendEvent) throws MessagingException {
      return new MimeMessageBuilder(session)
         .setFrom(data.getFrom())
         .setReplyTo(data.getReplyTo())
         .setRecipients(RecipientType.TO, data.getTo())
         .setRecipients(RecipientType.CC, data.getCc())
         .setRecipients(RecipientType.BCC, data.getBcc())
         .setSubject(data.getSubjectSerializer().toSerializable(appendEvent))
         .build();
   }

   protected SmtpManager(final String name, final Session session, final MimeMessage message, final MailManager.FactoryData data) {
      super(null, name);
      this.session = session;
      this.message = message;
      this.data = data;
      this.buffer = new CyclicBuffer<>(LogEvent.class, data.getBufferSize());
   }

   @Override
   public void add(LogEvent event) {
      this.buffer.add(event.toImmutable());
   }

   @Deprecated
   public static SmtpManager getSmtpManager(
      final Configuration config,
      final String to,
      final String cc,
      final String bcc,
      final String from,
      final String replyTo,
      final String subject,
      String protocol,
      final String host,
      final int port,
      final String username,
      final String password,
      final boolean isDebug,
      final String filterName,
      final int numElements,
      final SslConfiguration sslConfiguration
   ) {
      AbstractStringLayout.Serializer subjectSerializer = PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(subject).build();
      MailManager.FactoryData data = new MailManager.FactoryData(
         to, cc, bcc, from, replyTo, subject, subjectSerializer, protocol, host, port, username, password, isDebug, numElements, sslConfiguration, filterName
      );
      return (SmtpManager)getManager(data.getManagerName(), FACTORY, data);
   }

   @Override
   public void sendEvents(final Layout<?> layout, final LogEvent appendEvent) {
      if (this.message == null) {
         this.connect(appendEvent);
      }

      try {
         LogEvent[] priorEvents = this.removeAllBufferedEvents();
         byte[] rawBytes = this.formatContentToBytes(priorEvents, appendEvent, layout);
         String contentType = layout.getContentType();
         String encoding = this.getEncoding(rawBytes, contentType);
         byte[] encodedBytes = this.encodeContentToBytes(rawBytes, encoding);
         InternetHeaders headers = this.getHeaders(contentType, encoding);
         MimeMultipart mp = this.getMimeMultipart(encodedBytes, headers);
         String subject = this.data.getSubjectSerializer().toSerializable(appendEvent);
         this.sendMultipartMessage(this.message, mp, subject);
      } catch (IOException | RuntimeException | MessagingException var11) {
         this.logError("Caught exception while sending e-mail notification.", var11);
         throw new LoggingException("Error occurred while sending email", var11);
      }
   }

   LogEvent[] removeAllBufferedEvents() {
      return this.buffer.removeAll();
   }

   protected byte[] formatContentToBytes(final LogEvent[] priorEvents, final LogEvent appendEvent, final Layout<?> layout) throws IOException {
      ByteArrayOutputStream raw = new ByteArrayOutputStream();
      this.writeContent(priorEvents, appendEvent, layout, raw);
      return raw.toByteArray();
   }

   private void writeContent(final LogEvent[] priorEvents, final LogEvent appendEvent, final Layout<?> layout, final ByteArrayOutputStream out) throws IOException {
      this.writeHeader(layout, out);
      this.writeBuffer(priorEvents, appendEvent, layout, out);
      this.writeFooter(layout, out);
   }

   protected void writeHeader(final Layout<?> layout, final OutputStream out) throws IOException {
      byte[] header = layout.getHeader();
      if (header != null) {
         out.write(header);
      }
   }

   protected void writeBuffer(final LogEvent[] priorEvents, final LogEvent appendEvent, final Layout<?> layout, final OutputStream out) throws IOException {
      for (LogEvent priorEvent : priorEvents) {
         byte[] bytes = layout.toByteArray(priorEvent);
         out.write(bytes);
      }

      byte[] bytes = layout.toByteArray(appendEvent);
      out.write(bytes);
   }

   protected void writeFooter(final Layout<?> layout, final OutputStream out) throws IOException {
      byte[] footer = layout.getFooter();
      if (footer != null) {
         out.write(footer);
      }
   }

   protected String getEncoding(final byte[] rawBytes, final String contentType) {
      DataSource dataSource = new ByteArrayDataSource(rawBytes, contentType);
      return MimeUtility.getEncoding(dataSource);
   }

   protected byte[] encodeContentToBytes(final byte[] rawBytes, final String encoding) throws MessagingException, IOException {
      ByteArrayOutputStream encoded = new ByteArrayOutputStream();
      this.encodeContent(rawBytes, encoding, encoded);
      return encoded.toByteArray();
   }

   protected void encodeContent(final byte[] bytes, final String encoding, final ByteArrayOutputStream out) throws MessagingException, IOException {
      try (OutputStream encoder = MimeUtility.encode(out, encoding)) {
         encoder.write(bytes);
      }
   }

   protected InternetHeaders getHeaders(final String contentType, final String encoding) {
      InternetHeaders headers = new InternetHeaders();
      headers.setHeader("Content-Type", contentType + "; charset=UTF-8");
      headers.setHeader("Content-Transfer-Encoding", encoding);
      return headers;
   }

   protected MimeMultipart getMimeMultipart(final byte[] encodedBytes, final InternetHeaders headers) throws MessagingException {
      MimeMultipart mp = new MimeMultipart();
      MimeBodyPart part = new MimeBodyPart(headers, encodedBytes);
      mp.addBodyPart(part);
      return mp;
   }

   @Deprecated
   protected void sendMultipartMessage(final MimeMessage msg, final MimeMultipart mp) throws MessagingException {
      synchronized (msg) {
         msg.setContent(mp);
         msg.setSentDate(new Date());
         Transport.send(msg);
      }
   }

   protected void sendMultipartMessage(final MimeMessage msg, final MimeMultipart mp, final String subject) throws MessagingException {
      synchronized (msg) {
         msg.setContent(mp);
         msg.setSentDate(new Date());
         msg.setSubject(subject);
         Transport.send(msg);
      }
   }

   private synchronized void connect(final LogEvent appendEvent) {
      if (this.message == null) {
         try {
            this.message = createMimeMessage(this.data, this.session, appendEvent);
         } catch (MessagingException var3) {
            this.logError("Could not set SmtpAppender message options", var3);
            this.message = null;
         }
      }
   }

   public static class SMTPManagerFactory implements MailManagerFactory {
      public SmtpManager createManager(final String name, final MailManager.FactoryData data) {
         String smtpProtocol = data.getSmtpProtocol();
         String prefix = "mail." + smtpProtocol;
         Properties properties = PropertiesUtil.getSystemProperties();
         properties.setProperty("mail.transport.protocol", smtpProtocol);
         if (properties.getProperty("mail.host") == null) {
            properties.setProperty("mail.host", NetUtils.getLocalHostname());
         }

         String smtpHost = data.getSmtpHost();
         if (null != smtpHost) {
            properties.setProperty(prefix + ".host", smtpHost);
         }

         if (data.getSmtpPort() > 0) {
            properties.setProperty(prefix + ".port", String.valueOf(data.getSmtpPort()));
         }

         Authenticator authenticator = this.buildAuthenticator(data.getSmtpUsername(), data.getSmtpPassword());
         if (null != authenticator) {
            properties.setProperty(prefix + ".auth", "true");
         }

         if (smtpProtocol.equals("smtps")) {
            SslConfiguration sslConfiguration = data.getSslConfiguration();
            if (sslConfiguration != null) {
               SSLSocketFactory sslSocketFactory = sslConfiguration.getSslSocketFactory();
               properties.put(prefix + ".ssl.socketFactory", sslSocketFactory);
               properties.setProperty(prefix + ".ssl.checkserveridentity", Boolean.toString(sslConfiguration.isVerifyHostName()));
            }
         }

         Session session = Session.getInstance(properties, authenticator);
         session.setProtocolForAddress("rfc822", smtpProtocol);
         session.setDebug(data.isSmtpDebug());
         return new SmtpManager(name, session, null, data);
      }

      private Authenticator buildAuthenticator(final String username, final String password) {
         return null != password && null != username ? new Authenticator() {
            private final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username, password);

            protected PasswordAuthentication getPasswordAuthentication() {
               return this.passwordAuthentication;
            }
         } : null;
      }
   }
}
