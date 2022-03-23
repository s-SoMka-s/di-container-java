package cases.server;

import cases.server.controllers.MailController;
import cases.server.controllers.UsersController;
import framework.context.NewContext;

public class Server {
    public static void main (String[] args) {
        NewContext.Start(Server.class);

        var mailer = NewContext.getType(MailController.class);
        mailer.sendGoogleMail();
        mailer.sendYandexMail();
        mailer.printAllMails();

        var userController = NewContext.getType(UsersController.class);
        userController.addUser("Nick");
        userController.addUser("Tom");
        userController.allSayHi();
    }
}
