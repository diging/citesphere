package edu.asu.diging.citesphere.core.email.impl;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import edu.asu.diging.citesphere.core.exceptions.MailNotSetupException;

public class NotSetupMailSender implements JavaMailSender {

    @Override
    public void send(SimpleMailMessage arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public void send(SimpleMailMessage... arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public MimeMessage createMimeMessage() {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public MimeMessage createMimeMessage(InputStream arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public void send(MimeMessage arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public void send(MimeMessage... arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public void send(MimeMessagePreparator arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

    @Override
    public void send(MimeMessagePreparator... arg0) throws MailException {
        throw new MailNotSetupException("Mail sender has not been setup.");
    }

}