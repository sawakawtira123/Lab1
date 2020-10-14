import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExitAction extends AbstractAction {
    ExitAction() {
        putValue(NAME, "Выход");
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}