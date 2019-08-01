package ru.lulkast.controllers;

import java.sql.SQLException;

public interface UserController {
    String getUserById(String id) throws SQLException;

    String saveUser(String userName, String password) throws SQLException;

    String getAllUsers() throws SQLException;
}
