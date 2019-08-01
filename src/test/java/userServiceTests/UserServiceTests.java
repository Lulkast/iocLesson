package userServiceTests;

import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.lulkast.models.User;
import ru.lulkast.repositories.UserRepository;
import ru.lulkast.services.UserService;
import ru.lulkast.services.UserServiceFirstAttemtImplements;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTests {

    private static UserService userService;
    static User user;
    static User user2;

    @BeforeAll
    public static void init() throws SQLException {
        UserRepository userRepository = mock(UserRepository.class);
        user = new User("user", "password");
        user2 = new User("user2", "password2");
        HashSet<User> testSet = new HashSet<>();
        testSet.add(user);
        testSet.add(user2);
        when(userRepository.saveUser(user)).thenReturn(user);
        when(userRepository.saveUser(user2)).thenReturn(user2);
        when(userRepository.saveUser(null)).thenThrow(new SQLException());
        when(userRepository.findAll()).thenReturn(testSet);
        when(userRepository.findByUUID(user.getUuid())).thenReturn(user);
        userService = new UserServiceFirstAttemtImplements(userRepository);
    }

    @Test
    public void userServiceSaveTest() throws SQLException {
        User saved = userService.save(user);
        assertTrue(Objects.nonNull(saved));
        userService.save(user2);
        User empty = null;
        Throwable thrown = assertThrows(IllegalArgumentException.class, () -> userService.save(empty));
    }

    @Test
    public void UserServiceGetByUUIDTest() throws NotFoundException, SQLException {
        User findUser = userService.getByUUID(user.getUuid());
        assertTrue(findUser.equals(user));
        Throwable thrown = assertThrows(NotFoundException.class, () -> userService.getByUUID(UUID.randomUUID()));
    }

    @Test
    public void UserServiceGetAllTest() throws SQLException {
        Set<User> all = userService.getAll();
        HashSet<User> testSet = new HashSet<>();
        testSet.add(user);
        testSet.add(user2);
        assertIterableEquals(all, testSet);
    }
}
