package ru.lulkast.repositories;

import ru.lulkast.models.User;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public interface UserRepository {
    User findByUUID(UUID uuid) throws SQLException;

    User saveUser(User user) throws SQLException;

    Set<User> findAll() throws SQLException;

}
