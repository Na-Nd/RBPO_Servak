    package ru.mtuci.rbposervak.services;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import ru.mtuci.rbposervak.entities.ENUMS.session.STATUS;
    import ru.mtuci.rbposervak.entities.User;
    import ru.mtuci.rbposervak.entities.UserSession;
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
                throw new RuntimeException("Сессии пользователя заблокированы");
            }
    
            // Проверка на блокировку аккаунта
            if(user.getIsBlocked()){
                throw new RuntimeException("Аккаунт пользователя заблокирован");
            }
    
            // Проверка на активные сессии
            List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(user, STATUS.ACTIVE);
            if (!activeSessions.isEmpty()) {
                blockUserSessions(user);
                throw new RuntimeException("Обнаружено несколько активных сессий. Все сессии заблокированы.");
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
        public UserSession refreshAccessToken(String refreshToken) {
            // Проверка на наличие активной сессии
            UserSession session = userSessionRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new RuntimeException("Сессия не найдена"));
    
            if (session.getStatus() != STATUS.ACTIVE) {
                throw new RuntimeException("Сессия не активна");
            }
    
            // Проверка на наличие заблокированных сессий
            if (hasBlockedSessions(session.getUser())) {
                throw new RuntimeException("Невозможно обновить access токен: есть заблокированные сессии");
            }
    
            // Проверка на активные сессии
            List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(session.getUser(), STATUS.ACTIVE);
            if (activeSessions.size() > 1) { // Если больше одной - это подозрительная активность, соответственно блокируем
                blockUserSessions(session.getUser());
                throw new RuntimeException("Обнаружено несколько активных сессий. Все сессии заблокированы.");
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
                    .orElseThrow(() -> new RuntimeException("Сессия не найдена"));
    
            if(session.getStatus() != STATUS.ACTIVE){
                throw new RuntimeException("Сессия не активна");
            }
    
            // Проверка на активные сессии
            List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(session.getUser(), STATUS.ACTIVE);
            if (activeSessions.size() > 1) {
                blockUserSessions(session.getUser());
                throw new RuntimeException("Обнаружено несколько активных сессий. Все сессии заблокированы.");
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
                    .orElseThrow(() -> new RuntimeException("Сессия не найдена"));
    
            // Лишний раз проверяем на активность
            if (session.getStatus() != STATUS.ACTIVE) {
                throw new RuntimeException("Сессия уже неактивна");
            }
    
            session.setStatus(STATUS.INACTIVE);
            session.setLastActivityTime(LocalDateTime.now());
    
            userSessionRepository.save(session);
        }
    
        /// Поиск сессии по access токену
        public UserSession getSessionByAccessToken(String accessToken) {
            return userSessionRepository.findByAccessToken(accessToken)
                    .orElseThrow(() -> new RuntimeException("Сессия не найдена"));
        }
    
        /// Обновление времени последней активности для активной сессии пользователя
        public void updateLastActivityTime(User user) {
            // Поиск активной сессии пользователя
            List<UserSession> activeSessions = userSessionRepository.findByUserAndStatus(user, STATUS.ACTIVE);
    
            if (activeSessions.isEmpty()) {
                throw new RuntimeException("Активная сессия не найдена");
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
                    .orElseThrow(() -> new RuntimeException("Сессия не найдена"));
    
            // Проверяем статус
            boolean isActive = session.getStatus() == STATUS.ACTIVE;
            if (isActive) {
                System.out.println("Сессия активна");
            } else {
                System.out.println("Сессия неактивна");
            }
    
            return isActive;
        }
    }
