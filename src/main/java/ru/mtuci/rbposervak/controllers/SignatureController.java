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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// TODO try-catch'и
@RestController
@RequestMapping("/signatures")
public class SignatureController {
    private final SignatureService signatureService;

    @Autowired
    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    /// Получение всей базы (только актуальные)
    @GetMapping
    public ResponseEntity<List<Signature>> getAllActual() {
        return ResponseEntity.status(200).body(signatureService.getAllActualSignatures());
    }

    /// Получение диффа (все записи с указанной даты)
    @GetMapping("/modified-after")
    public ResponseEntity<List<Signature>> getModifiedAfter(@RequestParam LocalDateTime since) {
        return ResponseEntity.status(200).body(signatureService.getSignaturesModifiedAfter(since));
    }

    /// Получение по списку идентификаторов
    @PostMapping("/by-ids")
    public ResponseEntity<List<Signature>> getByIds(@RequestBody List<UUID> ids) {
        return ResponseEntity.status(200).body(signatureService.getSignaturesByIds(ids));
    }

    /// Добавление новой сигнатуры
    @PostMapping
    public ResponseEntity<?> createSignature(@Valid @RequestBody Signature signature, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.status(200).body(signatureService.addSignature(signature, userDetails));
        } catch (Exception e) {
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

    /// Удаление (смена статуса) сигнатуры
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSignature(@PathVariable UUID id, @RequestHeader("Authorization") String authBearer) {
        signatureService.deleteSignature(id, authBearer);
        return ResponseEntity.status(200).body("Успешное удаление сигнатуры");
    }

    /// Получение сигнатур по статусу
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<Signature>> getByStatus(@PathVariable STATUS status) {
        return ResponseEntity.status(200).body(signatureService.getSignaturesByStatus(status));
    }

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

        if (!signatureService.verifySignature(signature)) {
            signatureService.markAsCorrupted(id, "Ошибка верификации");
            return ResponseEntity.status(200).body("Сигнатура невалидна, помечена как  CORRUPTED.");
        }
        return ResponseEntity.status(200).body("Сигнатура валидна");
    }
}
