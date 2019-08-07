package ru.lulkast;

import org.lulkast.di.annotations.MyFrameworkBootStart;
import org.lulkast.di.core.IOCDiCoreFrameworc;
import ru.lulkast.controllers.UserController;
import java.sql.SQLException;

@MyFrameworkBootStart("ru.lulkast")
public class Main {

    public static void main(String[] args) throws SQLException {
        IOCDiCoreFrameworc.run(Main.class);
        UserController controller = IOCDiCoreFrameworc.getByInterface(UserController.class);
        controller.saveUser("user1r", "password1r");
        System.out.println(controller.getAllUsers());
    }
}
