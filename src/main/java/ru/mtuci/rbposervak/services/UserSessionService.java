package ru.mtuci.rbposervak.services;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mtuci.rbposervak.entities.DTO.TokenResponse;
import ru.mtuci.rbposervak.entities.ENUMS.session.STATUS;
import ru.mtuci.rbposervak.entities.User;
import ru.mtuci.rbposervak.entities.UserSession;
import ru.mtuci.rbposervak.entities.requests.UpdateSessionRequest;
import ru.mtuci.rbposervak.repositories.UserSessionRepository;
import ru.mtuci.rbposervak.utils.JwtUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-jwt.expiration}")
    private long refreshTokenExpiration;

    @Autowired
    public UserSessionService(UserSessionRepository userSessionRepository, JwtUtil jwtUtil) {
        this.userSessionRepository = userSessionRepository;
        this.jwtUtil = jwtUtil;
    }

    ///  Создание новой пользовательской сессии
    public UserSession createUserSession(User user) {
        // Проверка на наличие заблокированных сессий
        if(hasBlockedSessions(user)){
            throw new RuntimeException("User sessions are blocked");
        }

        // Проверка на блокировку аккаунта
        if(user.getIsBlocked()){
            throw new RuntimeException("The user's account has been blocked.");
        }

        // Проверка на активные сессии
        List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(user, STATUS.ACTIVE);
        if (!activeSessions.isEmpty()) {
            blockUserSessions(user);
            throw new RuntimeException("Multiple active sessions detected. All sessions blocked.");
        }

        UserSession session = UserSession.builder()
                .user(user)
                .accessToken(jwtUtil.generateAccessToken(user))
                .refreshToken(jwtUtil.generateRefreshToken(user))
                .accessTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(accessTokenExpiration)))
                .refreshTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)))
                .sessionCreationTime(LocalDateTime.now())
                .lastActivityTime(LocalDateTime.now())
                .status(STATUS.ACTIVE)
                .build();

        return userSessionRepository.save(session);
    }

    /// Наличие заблокированных сессий
    public boolean hasBlockedSessions(User user) {
        List<UserSession> blockedSessions = userSessionRepository.findByUserAndStatus(user, STATUS.BLOCKED);
        return !blockedSessions.isEmpty();
    }

    /// Обновление access токена
    public UserSession refreshAccessToken(String oldAccessToken) {
        // Проверка на наличие активной сессии
        UserSession session = userSessionRepository.findByAccessToken(oldAccessToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() != STATUS.ACTIVE) {
            throw new RuntimeException("Session is not active");
        }

        // Проверка на наличие заблокированных сессий
        if (hasBlockedSessions(session.getUser())) {
            throw new RuntimeException("Unable to refresh access token: there are blocked sessions");
        }

        // Проверка на активные сессии
        List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(session.getUser(), STATUS.ACTIVE);
        if (activeSessions.size() > 1) { // Если больше одной - это подозрительная активность, соответственно блокируем
            blockUserSessions(session.getUser());
            throw new RuntimeException("Multiple active sessions detected. All sessions blocked.");
        }

        // Проверка истечения refresh токена
        if (session.getRefreshTokenExpires().isBefore(LocalDateTime.now())) {
            // Обновляем refresh токен
            session.setRefreshToken(jwtUtil.generateRefreshToken(session.getUser()));
            session.setRefreshTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)));
        }

        // Обновляем access токен
        session.setAccessToken(jwtUtil.generateAccessToken(session.getUser()));
        session.setAccessTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(accessTokenExpiration)));
        session.setLastActivityTime(LocalDateTime.now());

        return userSessionRepository.save(session);
    }

    /// Обновление refresh токена
    public UserSession refreshRefreshToken(String refreshToken){
        // Проверка на наличие активной сессии
        UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if(session.getStatus() != STATUS.ACTIVE){
            throw new RuntimeException("Session is not active");
        }

        // Проверка на активные сессии
        List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(session.getUser(), STATUS.ACTIVE);
        if (activeSessions.size() > 1) {
            blockUserSessions(session.getUser());
            throw new RuntimeException("Multiple active sessions detected. All sessions blocked.");
        }

        // В случае если сессия активна и нет подозрительной активности, обновим refresh токен текущей сессии (нет надобности создавать новую сессию)
        session.setRefreshToken(jwtUtil.generateRefreshToken(session.getUser()));
        session.setRefreshTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)));
        session.setLastActivityTime(LocalDateTime.now());

        return userSessionRepository.save(session);
    }

    /// Блокировка активных сессий пользователя
    public void blockUserSessions(User user) {
        List<UserSession> sessions = userSessionRepository.findByUserAndStatus(user, STATUS.ACTIVE);
        sessions.forEach(session -> session.setStatus(STATUS.BLOCKED));

        userSessionRepository.saveAll(sessions);
    }

    /// Деактивация сессии по access токену
    public void deactivateSessionByAccessToken(String accessToken) {
        UserSession session = userSessionRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Лишний раз проверяем на активность
        if (session.getStatus() != STATUS.ACTIVE) {
            throw new RuntimeException("The session is no longer active");
        }

        session.setStatus(STATUS.INACTIVE);
        session.setLastActivityTime(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    /// Поиск сессии по access токену
    public UserSession getSessionByAccessToken(String accessToken) {
        return userSessionRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    /// Обновление времени последней активности для активной сессии пользователя
    public void updateLastActivityTime(User user) {
        // Поиск активной сессии пользователя
        List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(user, STATUS.ACTIVE);

        if (activeSessions.isEmpty()) {
            throw new RuntimeException("Active session not found");
        }

        UserSession session = activeSessions.getFirst();

        session.setLastActivityTime(LocalDateTime.now());

        userSessionRepository.save(session);
    }

    /// Активна ли сессия
    public Boolean isSessionActive(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        // Ищем сессию по токену
        UserSession session = userSessionRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Проверяем статус
        boolean isActive = session.getStatus() == STATUS.ACTIVE;
        if (isActive) {
            System.out.println("Сессия активна");
        } else {
            System.out.println("Сессия неактивна");
        }

        return isActive;
    }

    /// Поиск неактивный и помеченных на удаление сессий
    public void handleInactiveSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        List<UserSession> activeSessions = userSessionRepository.findByStatusAndLastActivityTimeBefore(STATUS.ACTIVE, threshold);

        if (!activeSessions.isEmpty()) {
            System.out.printf("Найдено %s активных сессий для перевода в статус INACTIVE", activeSessions.size());

            activeSessions.forEach(session -> {
                session.setStatus(STATUS.INACTIVE);
                session.setLastActivityTime(LocalDateTime.now()); // По это сути имитация логаута пользователя, поэтому установим LAT
            });

            userSessionRepository.saveAll(activeSessions);
            System.out.println("Активные сессии успешно переведены в статус INACTIVE");
        } else {
            System.out.println("Активных сессий для перевода в статус INACTIVE не найдено");
        }
    }

    /// Обработка INACTIVE и REVOKED сессий
    public void handlingInactiveAndRevokedSessions(){
        markSessionsAsRevoked();
        deleteRevokedSessions();
    }

    /// Пометка неактивных сессий на отзыв
    public void markSessionsAsRevoked() {
        // Порог - 1 День
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);

        List<UserSession> inactiveSessions = userSessionRepository.findByStatusAndLastActivityTimeBefore(STATUS.INACTIVE, threshold);

        if(!inactiveSessions.isEmpty()){
            System.out.printf("Найдено %s неактивных сессий для пометки на отзыв", inactiveSessions.size());

            inactiveSessions.forEach(session -> {
                session.setStatus(STATUS.REVOKED);
                session.setLastActivityTime(LocalDateTime.now());
            });

            userSessionRepository.saveAll(inactiveSessions);
            System.out.printf("%s сессий были помечены как REVOKED", inactiveSessions.size());
        } else {
            System.out.println("Неактивных сессий для пометки на отзыв не найдено");
        }
    }

    /// Удаление сессий, помеченных на отзыв (чтобы не хранились долго)
    public void deleteRevokedSessions(){
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);

        List<UserSession> revokedSessions = userSessionRepository.findByStatusAndLastActivityTimeBefore(STATUS.REVOKED, threshold);

        if(!revokedSessions.isEmpty()){
            System.out.printf("Найдено %s сессий для удаления", revokedSessions.size());
            userSessionRepository.deleteAll(revokedSessions);
            System.out.printf("%s сессий было удалено", revokedSessions.size());
        } else {
            System.out.println("Сессий для удаления не найдено");
        }
    }

    /// Обновление сессии администратором
    // мб пригодится isolation = Isolation.READ_UNCOMMITTED
    @Transactional()
    public UserSession updateSession(Long sessionId, UpdateSessionRequest sessionRequest){
        UserSession existingSession = userSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Сверяем версию (если сессия была изменена другим процессом)
        if(!existingSession.getVersion().equals(sessionRequest.getVersion())){
            throw new RuntimeException("Session version conflict");
        }

        // Обновляем поля
        existingSession.setAccessToken(sessionRequest.getAccessToken());
        existingSession.setRefreshToken(sessionRequest.getRefreshToken());
        existingSession.setSessionCreationTime(sessionRequest.getSessionCreationTime());
        existingSession.setLastActivityTime(sessionRequest.getLastActivityTime());
        existingSession.setStatus(sessionRequest.getStatus());
        existingSession.setVersion(sessionRequest.getVersion());

        return userSessionRepository.save(existingSession);
    }

    /// Обновление рефреша
    public TokenResponse updateRefreshToken(String refresh){
        // Находим сессию с таким рефрешем
        System.out.println("Пришел рефреш: " + refresh);
        UserSession session = userSessionRepository.findByRefreshToken(refresh)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() != STATUS.ACTIVE) {
            throw new RuntimeException("Session is not active");
        }

        // Проверка на наличие заблокированных сессий
        if (hasBlockedSessions(session.getUser())) {
            throw new RuntimeException("Unable to refresh access token: there are blocked sessions");
        }

        // Проверка на активные сессии
        List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(session.getUser(), STATUS.ACTIVE);
        if (activeSessions.size() > 1) { // Если больше одной - это подозрительная активность, соответственно блокируем
            blockUserSessions(session.getUser());
            throw new RuntimeException("Multiple active sessions detected. All sessions blocked.");
        }

        // Проверка истечения refresh токена
        if (session.getRefreshTokenExpires().isBefore(LocalDateTime.now())) {
            // Обновляем refresh токен
            session.setRefreshToken(jwtUtil.generateRefreshToken(session.getUser()));
            session.setRefreshTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpiration)));

            // и обновляем access
            session.setAccessToken(jwtUtil.generateAccessToken(session.getUser()));
            session.setAccessTokenExpires(LocalDateTime.now().plus(Duration.ofMillis(accessTokenExpiration)));
            session.setLastActivityTime(LocalDateTime.now());
        }

        // Меняем статус текущей сессии на inactive
        session.setStatus(STATUS.INACTIVE);

        userSessionRepository.save(session);

        return new TokenResponse(session.getAccessToken(), session.getRefreshToken(), null);
    }
}
