package ru.lulkast.services;

import javassist.NotFoundException;
import ru.lulkast.models.User;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    User getByUUID(UUID uuid) throws NotFoundException, SQLException;

    User save(User user) throws SQLException;

    Set<User> getAll() throws SQLException;
}
