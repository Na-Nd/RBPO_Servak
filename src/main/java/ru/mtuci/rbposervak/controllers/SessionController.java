package ru.mtuci.rbposervak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbposervak.entities.requests.UpdateSessionRequest;
import ru.mtuci.rbposervak.services.UserSessionService;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final UserSessionService sessionService;

    @Autowired
    public SessionController(UserSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @RequestBody UpdateSessionRequest sessionRequest) {
        try{
            return ResponseEntity.status(200).body(sessionService.updateSession(id, sessionRequest));
        } catch (RuntimeException e){
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

}
