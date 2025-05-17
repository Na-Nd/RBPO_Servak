package ru.mtuci.rbposervak.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbposervak.entities.ENUMS.signature.STATUS;
import ru.mtuci.rbposervak.entities.Signature;
import ru.mtuci.rbposervak.entities.SignatureAudit;
import ru.mtuci.rbposervak.services.SignatureService;
import ru.mtuci.rbposervak.utils.SignatureUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/signatures")
public class SignatureController {
    private final SignatureService signatureService;
    private final SignatureUtil signatureUtil;

    @Autowired
    public SignatureController(SignatureService signatureService, SignatureUtil signatureUtil) {
        this.signatureService = signatureService;
        this.signatureUtil = signatureUtil;
    }

    /// Получение всей базы (только актуальные)
    @GetMapping
    public ResponseEntity<?> getAllActual() {
        try{
            return ResponseEntity.status(200).body(signatureService.getAllActualSignatures());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /// Получение диффа (все записи с указанной даты)
    @GetMapping("/modified-after")
    public ResponseEntity<?> getModifiedAfter(@RequestParam LocalDateTime since) {
        try{
            return ResponseEntity.status(200).body(signatureService.getSignaturesModifiedAfter(since));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }

    /// Получение по списку идентификаторов
    @PostMapping("/by-ids")
    public ResponseEntity<?> getByIds(@RequestBody List<UUID> ids) {
        try{
            return ResponseEntity.status(200).body(signatureService.getSignaturesByIds(ids));
        } catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }

    /// Добавление новой сигнатуры
    @PostMapping
    public ResponseEntity<?> createSignature(@Valid @RequestBody Signature signature, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.status(200).body(signatureService.addSignature(signature, userDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /// Обновление существующей сигнатуры
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSignature(
            @PathVariable UUID id,
            @Valid @RequestBody Signature signature,
            @RequestHeader("Authorization") String authBearer) {

        try {
            return ResponseEntity.ok(signatureService.updateSignature(id, signature, authBearer));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    // TODO
    /// Удаление (смена статуса) сигнатуры
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSignature(@PathVariable UUID id, @RequestHeader("Authorization") String authBearer) {
        try{
            signatureService.deleteSignature(id, authBearer);
            return ResponseEntity.status(200).body("Signature deleted successfully");
        } catch (RuntimeException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }

    }

    /// Получение сигнатур по статусу
    @GetMapping("/by-status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable STATUS status) {
        try {
            return ResponseEntity.status(200).body(signatureService.getSignaturesByStatus(status));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }

    }

    // TODO
    /// Получение аудита по сигнатуре
    @GetMapping("/{id}/audit")
    public ResponseEntity<List<SignatureAudit>> getAudit(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(signatureService.getSignatureAudit(id));
    }

    /// Принудительная проверка ЭЦП
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifySignature(@PathVariable UUID id) {
        Signature signature = signatureService.getSignatureById(id);
        if (signature == null) {
            return ResponseEntity.status(404).body(null);
        }

        if (!signatureUtil.verifySignature(signature)) {
            signatureService.markAsCorrupted(id, "Validation error");
            return ResponseEntity.status(200).body("The signature is invalid, marked as CORRUPTED.");
        }
        return ResponseEntity.status(200).body("Signature is valid");
    }
}
