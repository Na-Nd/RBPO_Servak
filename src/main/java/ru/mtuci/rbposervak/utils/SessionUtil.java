package ru.mtuci.rbposervak.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mtuci.rbposervak.services.UserSessionService;

@Component
public class SessionUtil {
    private final UserSessionService userSessionService;

    @Autowired
    public SessionUtil(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Scheduled(fixedRate = 24*60*60*1000) // 24 Часа
    public void cleanSessions() {
        System.out.println("Запуск очистки INACTIVE и REVOKED сессий");
        userSessionService.handlingInactiveAndRevokedSessions();
        System.out.println("Отчистка сессий завершена");
    }

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000) // 2 Часа
    public void checkActiveSessions() {
        System.out.println("Запуск проверки активных сессий");
        userSessionService.handleInactiveSessions();
        System.out.println("Проверка активных сессий завершена");
    }
}
