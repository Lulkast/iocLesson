package ru.lulkast.userDB;

import java.sql.Connection;
import java.sql.SQLException;

public interface AppInterface {
    void createDbUserTable() throws SQLException;

    Connection connect();
}
