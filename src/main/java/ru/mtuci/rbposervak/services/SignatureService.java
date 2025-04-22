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

import java.security.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SignatureService {
    private final SignatureRepository signatureRepository;
    private final SignatureHistoryRepository historyRepository;
    private final SignatureAuditRepository auditRepository;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // TODO util
    /// Константа для периодической проверки
    private static final long VERIFICATION_INTERVAL_HOURS = 24;

    /// Добавление сигнатуры
    public Signature addSignature(Signature signature, UserDetails userDetails) {
        Long createdBy = userService.findUserByLogin(userDetails.getUsername()).getId();

        signature.setUpdatedAt(LocalDateTime.now());
        signature.setStatus(STATUS.ACTUAL);
        signSignature(signature);
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
                    updateChangedFields(existing, updatedSignature);

                    // Подписываем новую версию
                    signSignature(existing);

                    // Сохраняем в основную таблицу
                    Signature saved = signatureRepository.save(existing);

                    // И создаем новую запись в аудите
                    logAudit(saved, changedBy, ChangeType.UPDATED, getChangedFields(existing, updatedSignature));

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

    // TODO util
    /// Обновление измененный полей
    private void updateChangedFields(Signature existing, Signature updated) {
        if (updated.getThreatName() != null) existing.setThreatName(updated.getThreatName());
        if (updated.getFirstBytes() != null) existing.setFirstBytes(updated.getFirstBytes());
        if (updated.getRemainderHash() != null) existing.setRemainderHash(updated.getRemainderHash());
        if (updated.getRemainderLength() != 0) existing.setRemainderLength(updated.getRemainderLength());
        if (updated.getFileType() != null) existing.setFileType(updated.getFileType());
        if (updated.getOffsetStart() != 0) existing.setOffsetStart(updated.getOffsetStart());
        if (updated.getOffsetEnd() != 0) existing.setOffsetEnd(updated.getOffsetEnd());
        existing.setUpdatedAt(LocalDateTime.now());
    }

    // TODO util
    /// Список изменений
    private String getChangedFields(Signature oldVersion, Signature newVersion) {
        StringBuilder changes = new StringBuilder();

        // Сравнение threatName
        if (!Objects.equals(oldVersion.getThreatName(), newVersion.getThreatName())) {
            changes.append("threatName: ")
                    .append(oldVersion.getThreatName())
                    .append(" -> ")
                    .append(newVersion.getThreatName())
                    .append("; ");
        }

        // Сравнение firstBytes (сравниваем массивы байт)
        if (!Arrays.equals(oldVersion.getFirstBytes(), newVersion.getFirstBytes())) {
            changes.append("firstBytes: [changed]; ");
        }

        // Сравнение remainderHash
        if (!Objects.equals(oldVersion.getRemainderHash(), newVersion.getRemainderHash())) {
            changes.append("remainderHash: ")
                    .append(oldVersion.getRemainderHash())
                    .append(" -> ")
                    .append(newVersion.getRemainderHash())
                    .append("; ");
        }

        // Сравнение remainderLength
        if (oldVersion.getRemainderLength() != newVersion.getRemainderLength()) {
            changes.append("remainderLength: ")
                    .append(oldVersion.getRemainderLength())
                    .append(" -> ")
                    .append(newVersion.getRemainderLength())
                    .append("; ");
        }

        // Сравнение fileType
        if (!Objects.equals(oldVersion.getFileType(), newVersion.getFileType())) {
            changes.append("fileType: ")
                    .append(oldVersion.getFileType())
                    .append(" -> ")
                    .append(newVersion.getFileType())
                    .append("; ");
        }

        // Сравнение offsetStart
        if (oldVersion.getOffsetStart() != newVersion.getOffsetStart()) {
            changes.append("offsetStart: ")
                    .append(oldVersion.getOffsetStart())
                    .append(" -> ")
                    .append(newVersion.getOffsetStart())
                    .append("; ");
        }

        // Сравнение offsetEnd
        if (oldVersion.getOffsetEnd() != newVersion.getOffsetEnd()) {
            changes.append("offsetEnd: ")
                    .append(oldVersion.getOffsetEnd())
                    .append(" -> ")
                    .append(newVersion.getOffsetEnd())
                    .append("; ");
        }

        // Сравнение digitalSignature
        if (!Objects.equals(oldVersion.getDigitalSignature(), newVersion.getDigitalSignature())) {
            changes.append("digitalSignature: [changed]; ");
        }

        // Сравнение status
        if (oldVersion.getStatus() != newVersion.getStatus()) {
            changes.append("status: ")
                    .append(oldVersion.getStatus())
                    .append(" -> ")
                    .append(newVersion.getStatus())
                    .append("; ");
        }

        return changes.toString().trim();
    }

    // TODO util
    /// Вычисление хэша для подписи и сама подпись
    private void signSignature(Signature signature) {
        try {
            String data = buildSignableData(signature);
            byte[] hash = calculateHash(data);
            byte[] digitalSignature = signData(hash);
            signature.setDigitalSignature(Base64.getEncoder().encodeToString(digitalSignature));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при подписании сигнатуры: " + e.getMessage());
        }
    }

    //TODO util
    /// Проверка подписи
    public boolean verifySignature(Signature signature) {
        try {
            String dataToVerify = buildSignableData(signature);
            byte[] hash = calculateHash(dataToVerify);
            byte[] signatureBytes = Base64.getDecoder().decode(signature.getDigitalSignature());

            // Используем сигнатуру из java.security.Signature для подписания (полное имя класса чтоб не путалось)
            java.security.Signature sig = java.security.Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(hash);
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при проверке подписи", e);
        }
    }

    /// Получение всех сигнатур
    public List<Signature> getAllSignatures() {
        return signatureRepository.findAll();
    }

    /// Получение всех актуальных сигнатур
    public List<Signature> getAllActualSignatures() {
        return signatureRepository.findAllActual();
    }

    /// Получение сигнатур по списку идентификаторов
    public List<Signature> getSignaturesByIds(List<UUID> ids) {
        return signatureRepository.findByIdIn(ids);
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
        return signatureRepository.findByUpdatedAtAfter(date);
    }

    // TODO util
    /// Формирование данных для подписи
    private String buildSignableData(Signature signature) {
        return String.join("|",
                signature.getThreatName(),
                Base64.getEncoder().encodeToString(signature.getFirstBytes()),
                signature.getRemainderHash(),
                String.valueOf(signature.getRemainderLength()),
                signature.getFileType(),
                String.valueOf(signature.getOffsetStart()),
                String.valueOf(signature.getOffsetEnd()),
                signature.getUpdatedAt().toString());
    }

    // TODO util
    /// Вычисление хэша
    private byte[] calculateHash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data.getBytes());
    }

    // TODO util
    /// Подпись
    private byte[] signData(byte[] hash) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
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

    // TODO util
    /// Периодическая проверка ЭЦП
    @Scheduled(fixedRate = VERIFICATION_INTERVAL_HOURS * 60 * 60 * 1000)
    public void verifySignaturesPeriodically() {
        LocalDateTime lastCheckTime = LocalDateTime.now().minusHours(VERIFICATION_INTERVAL_HOURS);
        List<Signature> signaturesToCheck = signatureRepository
                .findByUpdatedAtAfterAndStatus(lastCheckTime, STATUS.ACTUAL);

        signaturesToCheck.forEach(signature -> {
            if (!verifySignature(signature)) {
                markAsCorrupted(signature.getId(), "Ошибка проверки эцп сигнатуры: " + signature.getId());
            }
        });
    }

    /// Получение записей по статусу
    public List<Signature> getSignaturesByStatus(STATUS status) {
        return signatureRepository.findByStatus(status);
    }

    /// Получение аудита по сигнатуре
    public List<SignatureAudit> getSignatureAudit(UUID signatureId) {
        return auditRepository.findBySignatureIdOrderByChangedAtDesc(signatureId);
    }
}