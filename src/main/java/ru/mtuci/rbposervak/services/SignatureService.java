package ru.mtuci.rbposervak.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mtuci.rbposervak.entities.ENUMS.signature.ChangeType;
import ru.mtuci.rbposervak.entities.ENUMS.signature.STATUS;
import ru.mtuci.rbposervak.entities.Signature;
import ru.mtuci.rbposervak.entities.SignatureAudit;
import ru.mtuci.rbposervak.entities.SignatureHistory;
import ru.mtuci.rbposervak.repositories.SignatureAuditRepository;
import ru.mtuci.rbposervak.repositories.SignatureHistoryRepository;
import ru.mtuci.rbposervak.repositories.SignatureRepository;
import ru.mtuci.rbposervak.utils.JwtUtil;
import ru.mtuci.rbposervak.utils.SignatureUtil;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SignatureService {
    private final SignatureRepository signatureRepository;
    private final SignatureHistoryRepository historyRepository;
    private final SignatureAuditRepository auditRepository;
    private final SignatureUtil signatureUtil;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /// Константа для периодической проверки
    private static final long VERIFICATION_INTERVAL_HOURS = 24;

    /// Добавление сигнатуры
    public Signature addSignature(Signature signature, UserDetails userDetails) {
        Long createdBy = userService.findUserByLogin(userDetails.getUsername()).getId();

        signature.setUpdatedAt(LocalDateTime.now());
        signature.setStatus(STATUS.ACTUAL);
        signatureUtil.signSignature(signature);
        Signature saved = signatureRepository.save(signature);
        logAudit(saved, createdBy, ChangeType.CREATED, "Создана новая сигнатура");
        return saved;
    }

    /// Обновление сигнатуры
    public Signature updateSignature(UUID id, Signature updatedSignature, String authBearer) {

        authBearer = authBearer.substring(7);
        authBearer = jwtUtil.extractUsername(authBearer);

        // AdminId
        Long changedBy = userService.findUserByLogin(authBearer).getId();

        return signatureRepository.findById(id)
                .map(existing -> {
                    // Сохраняем текущую (старую) сигнатуру в историю
                    saveToHistory(existing);

                    // Обновляем поля
                    signatureUtil.updateChangedFields(existing, updatedSignature);

                    // Подписываем новую версию
                    signatureUtil.signSignature(existing);

                    // Сохраняем в основную таблицу
                    Signature saved = signatureRepository.save(existing);

                    // И создаем новую запись в аудите
                    logAudit(saved, changedBy, ChangeType.UPDATED, signatureUtil.getChangedFields(existing, updatedSignature));

                    return saved;
                }).orElseThrow(() -> new RuntimeException("Сигнатура не найдена"));
    }

    /// Сохранение старой сигнатуры
    private void saveToHistory(Signature signature) {
        SignatureHistory history = SignatureHistory.builder()
                .signature(signature)
                .threatName(signature.getThreatName())
                .firstBytes(signature.getFirstBytes())
                .remainderHash(signature.getRemainderHash())
                .remainderLength(signature.getRemainderLength())
                .fileType(signature.getFileType())
                .offsetStart(signature.getOffsetStart())
                .offsetEnd(signature.getOffsetEnd())
                .digitalSignature(signature.getDigitalSignature())
                .updatedAt(signature.getUpdatedAt())
                .status(signature.getStatus())
                .build();
        historyRepository.save(history);
    }

    /// Формирование записи в аудите сигнатур
    private void logAudit(Signature signature, Long changedBy, ChangeType changeType, String fieldsChanged) {

        SignatureAudit audit = SignatureAudit.builder()
                .signature(signature)
                .changedBy(userService.getUserById(changedBy))
                .changedAt(LocalDateTime.now())
                .changeType(changeType)
                .fieldsChanged(fieldsChanged)
                .build();
        auditRepository.save(audit);
    }

    /// Получение всех сигнатур (мб понадобится)
    public List<Signature> getAllSignatures() {
        return signatureRepository.findAll();
    }

    /// Получение всех актуальных сигнатур
    public List<Signature> getAllActualSignatures() {
        List<Signature> signatures = signatureRepository.findAllActual();
        if(signatures.isEmpty()) {
            throw new RuntimeException("Актуальные сигнатуры не найдены");
        }
        return signatures;
    }

    /// Получение сигнатур по списку идентификаторов
    public List<Signature> getSignaturesByIds(List<UUID> ids) {
        List<Signature> signatures = signatureRepository.findByIdIn(ids);
        if(signatures.isEmpty()) {
            throw new RuntimeException("Сигнатуры с идентификаторами: " + ids + " не найдены");
        }
        return signatures;
    }

    /// Удаление (смена статуса) сигнатуры
    public void deleteSignature(UUID id, String deletedBy) {
        Signature signature = signatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сигнатура не найдена"));

        deletedBy = deletedBy.substring(7);
        Long adminId = userService.findUserByLogin(deletedBy).getId();

        // Сохраняем в историю перед изменением
        saveToHistory(signature);

        signature.setStatus(STATUS.DELETED);
        signature.setUpdatedAt(LocalDateTime.now());
        signatureRepository.save(signature);

        logAudit(signature, adminId, ChangeType.DELETED, "Сигнатура помечена как удаленная");
    }

    /// Получение конкретной сигнатуры
    public Signature getSignatureById(UUID id) {
        return signatureRepository.findById(id).orElse(null);
    }

    /// Получение изменений после конкретной даты
    public List<Signature> getSignaturesModifiedAfter(LocalDateTime date) {
        List<Signature> signatures = signatureRepository.findByUpdatedAtAfter(date);
        if(signatures.isEmpty()) {
            throw new RuntimeException("Изменения после даты: " + date + " не найдены");
        }
        return signatures;
    }

    /// Пометка как CORRUPTED
    public void markAsCorrupted(UUID id, String reason) {
        Signature signature = signatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сигнатура не найдена"));

        saveToHistory(signature);
        signature.setStatus(STATUS.CORRUPTED);
        signature.setUpdatedAt(LocalDateTime.now());
        signatureRepository.save(signature);

        logAudit(signature, null, ChangeType.CORRUPTED, "Сигнатура помечена как CORRUPTED: " + reason);
    }

    /// Периодическая проверка ЭЦП
    @Scheduled(fixedRate = VERIFICATION_INTERVAL_HOURS * 60 * 60 * 1000)
    public void verifySignaturesPeriodically() {
        LocalDateTime lastCheckTime = LocalDateTime.now().minusHours(VERIFICATION_INTERVAL_HOURS);
        List<Signature> signaturesToCheck = signatureRepository
                .findByUpdatedAtAfterAndStatus(lastCheckTime, STATUS.ACTUAL);

        signaturesToCheck.forEach(signature -> {
            if (!signatureUtil.verifySignature(signature)) {
                markAsCorrupted(signature.getId(), "Ошибка проверки эцп сигнатуры: " + signature.getId());
            }
        });
    }

    /// Получение записей по статусу
    public List<Signature> getSignaturesByStatus(STATUS status) {
        return signatureRepository.findByStatus(status)
                .orElseThrow(() -> new RuntimeException("Записи со статусом " + status.toString() + " не найдены"));
    }

    /// Получение аудита по сигнатуре
    public List<SignatureAudit> getSignatureAudit(UUID signatureId) {
        return auditRepository.findBySignatureIdOrderByChangedAtDesc(signatureId);
    }
}