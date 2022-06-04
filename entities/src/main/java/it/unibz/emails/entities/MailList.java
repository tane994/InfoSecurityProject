package it.unibz.emails.entities;

import java.util.List;

public class MailList {
    private final List<Mail> mails;


    public MailList(List<Mail> mails) {
        this.mails = mails;
    }


    public List<Mail> getMails() {
        return mails;
    }
}
