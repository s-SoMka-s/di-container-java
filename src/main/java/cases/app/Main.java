package cases.app;

import cases.app.controllers.MailController;
import implementation.context.NewContext;

public class Main {
    public static void main (String[] args) {
        NewContext.Start(Main.class);

        var controller = NewContext.getType(MailController.class);
        controller.SendMail();
    }
}
