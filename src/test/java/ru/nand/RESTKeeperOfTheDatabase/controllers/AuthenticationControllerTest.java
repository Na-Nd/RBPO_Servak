package ru.nand.RESTKeeperOfTheDatabase.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.nand.RESTKeeperOfTheDatabase.util.AuthenticationRequest;
import ru.nand.RESTKeeperOfTheDatabase.util.AuthenticationResponse;
import ru.nand.RESTKeeperOfTheDatabase.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks AuthenticationController authenticationController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAuthenticationTokenSuccess() throws Exception{
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@mail.com");
        request.setPassword("pass");

        UserDetails userDetails = User.withUsername("test@mail.com")
                .password("pass")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername(eq("test@mail.com"))).thenReturn(userDetails);
        when(jwtUtil.generateToken(eq(userDetails))).thenReturn("jwt-token");

        ResponseEntity<?> responseEntity = authenticationController.createAuthenticationToken(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("jwt-token", ((AuthenticationResponse) responseEntity.getBody()).getJwt());
    }

    @Test
    public void testCreateAuthenticationTokenBadCredentials() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@mail.com");
        request.setPassword("wrong-password");

        doThrow(new BadCredentialsException("Bad credentials")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        try {
            authenticationController.createAuthenticationToken(request);
        } catch (Exception e) {
            assertEquals("Incorrect username or password: ", e.getMessage());
        }
    }



}
