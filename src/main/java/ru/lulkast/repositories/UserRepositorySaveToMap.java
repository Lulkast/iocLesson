package ru.lulkast.repositories;

import org.lulkast.di.annotations.Component;
import org.lulkast.di.annotations.Inject;
import ru.lulkast.models.User;
import ru.lulkast.userDB.AppInterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class UserRepositorySaveToMap implements UserRepository {
    private AppInterface appInterface;

    @Inject
    public UserRepositorySaveToMap (AppInterface appInterface){
        this.appInterface = appInterface;
    }

    @Override
    public User findByUUID(UUID uuid) throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;
        User user = null;
        System.out.println(uuid.toString());
        String selectTableSQL = "SELECT USER_UUID , USERNAME, USER_PASSWORD FROM DBUSER " +
                "WHERE USER_UUID = " + "'" + uuid + "'";
        try {
            dbConnection = appInterface.connect();
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {
                String userid = rs.getString("USER_UUID");
                String username = rs.getString("USERNAME");
                String pass = rs.getString("USER_PASSWORD");
                user = new User(UUID.fromString(userid), username, pass);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
        return user;
    }


    @Override
    public User saveUser(User user) throws SQLException {
        Connection dbConnection = null;
        Statement statement = null;
        UUID id = user.getUuid();
        String name = user.getUserName();
        String password = user.getMd5Password();
        String insertTableSQL = "INSERT INTO DBUSER(USER_UUID, USERNAME, USER_PASSWORD)"
                + "VALUES ( " + "'" + id + "'" + "," + "'" + name + "'" + "," + "'" + password + "'" + ")";
        try {
            dbConnection = appInterface.connect();
            statement = dbConnection.createStatement();
            statement.executeUpdate(insertTableSQL);
            System.out.println("user saved!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        }
        return user;
    }

    @Override
    public Set<User> findAll() throws SQLException {
        Set<User> users = new HashSet<>();
        String selectTableSQL = "SELECT USER_UUID, USERNAME, USER_PASSWORD from DBUSER";
        Connection dbConnection = null;
        Statement statement = null;
        try {
            dbConnection = appInterface.connect();
            statement = dbConnection.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                String userid = rs.getString("USER_UUID");
                String username = rs.getString("USERNAME");
                String pass = rs.getString("USER_PASSWORD");
                users.add(new User(UUID.fromString(userid), username, pass));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
            return users;
        }
    }
}