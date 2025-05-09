package ru.mtuci.rbposervak.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mtuci.rbposervak.entities.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Component
public class SignatureUtil {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Autowired
    public SignatureUtil(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    /// Подпись
    private byte[] signData(byte[] hash) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
    }

    /// Вычисление хэша для подписи и сама подпись
    public void signSignature(Signature signature) {
        try {
            String data = buildSignableData(signature);
            byte[] hash = calculateHash(data);
            byte[] digitalSignature = signData(hash);
            signature.setDigitalSignature(Base64.getEncoder().encodeToString(digitalSignature));
        } catch (Exception e) {
            throw new RuntimeException("Error signing signature: " + e.getMessage());
        }
    }

    /// Проверка подписи
    public boolean verifySignature(Signature signature) {
        try {
            String dataToVerify = buildSignableData(signature);
            System.out.println("Data for verification: " + dataToVerify); // Логируем данные

            byte[] hash = calculateHash(dataToVerify);
            byte[] signatureBytes = Base64.getDecoder().decode(signature.getDigitalSignature());

            java.security.Signature sig = java.security.Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(hash);
            boolean isValid = sig.verify(signatureBytes);

            System.out.println("Signature verification result: " + isValid); // Логируем результат
            return isValid;
        } catch (Exception e) {
            System.out.println("Verification error: " + e.getMessage()); // Логируем ошибку
            throw new RuntimeException("Error verifying signature", e);
        }
    }

    /// Список изменений
    public String getChangedFields(Signature oldVersion, Signature newVersion) {
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

    /// Обновление измененный полей
    public void updateChangedFields(Signature existing, Signature updated) {
        if (updated.getThreatName() != null) existing.setThreatName(updated.getThreatName());
        if (updated.getFirstBytes() != null) existing.setFirstBytes(updated.getFirstBytes());
        if (updated.getRemainderHash() != null) existing.setRemainderHash(updated.getRemainderHash());
        if (updated.getRemainderLength() != 0) existing.setRemainderLength(updated.getRemainderLength());
        if (updated.getFileType() != null) existing.setFileType(updated.getFileType());
        if (updated.getOffsetStart() != 0) existing.setOffsetStart(updated.getOffsetStart());
        if (updated.getOffsetEnd() != 0) existing.setOffsetEnd(updated.getOffsetEnd());
        existing.setUpdatedAt(LocalDateTime.now());
    }

    /// Формирование данных для подписи
    private String buildSignableData(Signature signature) {
        return String.join("|",
                signature.getThreatName(),
                Base64.getEncoder().encodeToString(signature.getFirstBytes()),
                signature.getRemainderHash(),
                String.valueOf(signature.getRemainderLength()),
                signature.getFileType(),
                String.valueOf(signature.getOffsetStart()),
                String.valueOf(signature.getOffsetEnd()));
    }

    /// Вычисление хэша
    private byte[] calculateHash(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data.getBytes());
    }
}


