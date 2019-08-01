package ru.lulkast;

import org.lulkast.di.annotations.MyFrameworkBootStart;
import org.lulkast.di.core.IOCDiCoreFrameworc;
import ru.lulkast.controllers.UserController;
import java.sql.SQLException;

@MyFrameworkBootStart("ru.lulkast")
public class Main {

    /*
        //!!!делаем тесты сервиса и репозитория (не забываем про исключения) смотрим апи джейюнита и аннотацию тест
        //md5 do
*/

    public static void main(String[] args) throws SQLException {
        IOCDiCoreFrameworc.run(Main.class);
        UserController controller = IOCDiCoreFrameworc.getByInterface(UserController.class);
        controller.saveUser("aaaqqrrryy", "hhhrrrqqyyq");
        System.out.println(controller.getAllUsers());
    }
}
