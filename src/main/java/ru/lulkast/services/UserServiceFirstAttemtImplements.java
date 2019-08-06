package ru.lulkast.services;

import com.google.common.base.Strings;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;
import ru.lulkast.models.User;
import ru.lulkast.repositories.UserRepository;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component

public class UserServiceFirstAttemtImplements implements UserService {
    private UserRepository repository;

    @Inject
    public UserServiceFirstAttemtImplements (UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getByUUID(UUID uuid) throws NotFoundException, SQLException {
        User foundUser = repository.findByUUID(uuid);
        if (Objects.isNull(foundUser)) throw new NotFoundException("User is not found");
        return foundUser;
    }

    @Override
    public User save(User user) throws SQLException {
        try {
            if (Objects.isNull(user) ||
                    Objects.isNull(user.getUuid()) ||
                    Strings.isNullOrEmpty(user.getUserName())||
                    Strings.isNullOrEmpty(user.getMd5Password())) throw new IllegalArgumentException();
        }
        catch (NullPointerException e){
            System.out.println("ctch in us");
            e.printStackTrace();
        }

        return repository.saveUser(user);
    }

    @Override
    public Set<User> getAll() throws SQLException {
        return repository.findAll();
    }
}
