package ru.archertech.lab.telegram.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import ru.archertech.lab.telegram.model.TelegramUser;

import java.util.Optional;

@ApplicationScoped
@Transactional
public class TelegramUserService {

    public Optional<TelegramUser> findById(long id) {
        return Optional.ofNullable(TelegramUser.findById(id));
    }

    public void persist(TelegramUser telegramUser) {
        telegramUser.persist();
    }
}
