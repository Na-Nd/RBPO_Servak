package ru.mtuci.rbposervak.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mtuci.rbposervak.services.UserService;
import ru.mtuci.rbposervak.services.UserSessionService;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = jwtUtil.resolveToken(request);

            // Истек ли токен
            if(!jwtUtil.validateExpirationToken(token)){
                System.out.println("Токен истек, генерируем новый");
                String newAccess = userSessionService.refreshAccessToken(token).getAccessToken(); // Обновляем токен в сессии и возвращаем обновленный

                // Если при обновлении не вылетали исключения и новый access токен не пустой
                if (newAccess != null) {
                    System.out.println("Токен был успешно обновлен");
                    token = newAccess;

                    // Для удобства положим новый токен в заголовок ответа, чтобы не делать эндпоинт для получения нового токена
                    response.setHeader("Authorization", "Bearer " + token);
                } else {
                    // Если что-то не получилось, завершаем запрос с ошибкий
                    System.out.println("Токен обновить не удалось");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token has expired and cannot be renewed.");
                    return;
                }
            }

            // Если токен не истек и текущая сессия активна, то валидируем его и добавляем пользователя в КБ
            if(jwtUtil.validateToken(token) && userSessionService.isSessionActive(token)){
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = new User(
                            username,
                            "", // Нет надобности класть пароль пользователя в КБ
                            Collections.singleton(new SimpleGrantedAuthority(role))
                    );

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("User in filter: " + username + ", role: " + role);
                }
            } else {
                System.out.println("Токен не прошел валидацию");
            }

        } catch (Exception e){
            System.out.println("Ошибка в фильтре: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

//    /// Тест
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        System.out.println("Скип эндпоинта");
//        return request.getRequestURI().startsWith("/auth/");
//    }
}
