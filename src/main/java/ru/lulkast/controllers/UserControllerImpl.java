package ru.lulkast.controllers;

import com.google.common.base.Strings;
import javassist.NotFoundException;
import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;
import ru.lulkast.esceptions.WrongArgumentException;
import ru.lulkast.models.User;
import ru.lulkast.services.UserService;

import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserControllerImpl implements UserController {
    private UserService service;

    @Inject
    public UserControllerImpl (UserService service){
        System.out.println("const work");
        this.service = service;
    }

    public UserControllerImpl(){}

    @Override
    public String getUserById(String id) throws SQLException {
        if (Strings.isNullOrEmpty(id)) throw new WrongArgumentException("Null cant be applied");
        try {
            UUID uuid = UUID.fromString(id);
            return service.getByUUID(uuid).toString();
        } catch (NotFoundException e) {
            return e.getMessage();
        }
    }

    @Override
    public String saveUser(String userName, String password) throws SQLException {
        User user = new User(UUID.randomUUID(), userName, password);
        try {
            User saved = service.save(user);
            return saved.toString();
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @Override
    public String getAllUsers() throws SQLException {
        return service.getAll().stream().map(User::toString).collect(Collectors.joining(","));
    }
}
