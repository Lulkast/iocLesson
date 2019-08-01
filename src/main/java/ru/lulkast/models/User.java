package ru.lulkast.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    private static final long serialVersionUID = 1L;

    private UUID uuid;
    private String userName;
    private String md5Password;

    public User(UUID uuid, String userName, String md5Password) {
        this.uuid = uuid;
        this.userName = userName;
        this.md5Password = md5Password;
    }

    public User( String userName, String md5Password) {
        this.uuid = UUID.randomUUID();
        this.userName = userName;
        this.md5Password = md5Password;
    }
}
