package cases.app.controllers;

import cases.app.servicies.MailService;

import javax.inject.Inject;

public class MailController {
    private final MailService service;

    @Inject
    public MailController(MailService service) {
        this.service = service;
    }

    public void SendMail() {
        this.service.sendMail();
    }
}
