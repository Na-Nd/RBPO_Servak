package ru.mtuci.antivirus.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mtuci.antivirus.services.UserService;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtRequestFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = resolveToken(request);

            if(token != null && jwtUtil.validateToken(token)){ /// If token is valid
                String username = jwtUtil.extractLogin(token);

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails userDetails = userService.findUserByLogin(username);
                    System.out.println("JwtRequestFilter: doFilterInternal: Someone entered filter, login: " + userDetails.getUsername());
                    SecurityContextHolder.getContext().setAuthentication(jwtUtil.getAuthentication(token, userDetails));
                }
            }
        } catch (Exception e){
            System.err.println("JWT Filter error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
        System.out.println("JwtRequestFilter: doFilterInternal: Someone passed filter");
    }

    // Resolve token used in the request to cut off the "Bearer " part
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
