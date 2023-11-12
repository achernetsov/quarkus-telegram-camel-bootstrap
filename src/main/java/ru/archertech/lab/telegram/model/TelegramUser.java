package ru.archertech.lab.telegram.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@SuppressWarnings("unused")
@Entity
@Table(name = "telegram_user")
public class TelegramUser extends PanacheEntityBase {
    @Id
    public Long id;
    public String username;
    public String firstName;
    @SuppressWarnings("unused")
    private TelegramUser() {
    }

    public TelegramUser(Long id, String username, String firstName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }
}
