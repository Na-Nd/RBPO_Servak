package ru.mtuci.rbposervak.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mtuci.rbposervak.entities.DTO.SignatureBinaryDto;
import ru.mtuci.rbposervak.entities.Signature;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.List;

@Component
public class SignatureExportUtil {
    private final PrivateKey privateKey;

    @Autowired
    public SignatureExportUtil(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    /// Создание манифеста
//    public byte[] createManifest(List<Signature> signatures) throws Exception {
//        try (ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
//             DataOutputStream manifestData = new DataOutputStream(manifestStream)) {
//
//            // Количество сигнатур
//            manifestData.writeInt(signatures.size());
//
//            // Массив digital_signature
//            for (Signature signature : signatures) {
//                manifestData.writeUTF(signature.getId().toString()); // id сигнатуры
//                manifestData.writeUTF(signature.getDigitalSignature()); // её эцп
//            }
//
//            // Подпись манифеста
//            byte[] manifestContent = manifestStream.toByteArray();
//            byte[] manifestSignature = signData(calculateHash(manifestContent));
//            manifestData.write(manifestSignature);
//            System.out.println(Arrays.toString(manifestSignature));
//
//            return manifestContent;
//        }
//    }
    public byte[] createManifest(List<Signature> signatures) throws Exception {
        try (ByteArrayOutputStream manifestStream = new ByteArrayOutputStream();
             DataOutputStream manifestData = new DataOutputStream(manifestStream)) {

            // Количество сигнатур
            manifestData.writeInt(signatures.size());

            // Массив digital_signature
            for (Signature signature : signatures) {
                manifestData.writeUTF(signature.getId().toString());
                manifestData.writeUTF(signature.getDigitalSignature());
            }

            // Получаем данные БЕЗ подписи
            byte[] manifestContent = manifestStream.toByteArray();

            // Подписываем
            byte[] manifestSignature = signData(calculateHash(manifestContent));

            // Создаём новый поток для полного манифеста
            ByteArrayOutputStream fullManifest = new ByteArrayOutputStream();
            DataOutputStream fullData = new DataOutputStream(fullManifest);

            // Записываем оригинальные данные и подпись
            fullData.write(manifestContent);
            fullData.write(manifestSignature);

            System.out.println("Signature: " + Arrays.toString(manifestSignature));

            return fullManifest.toByteArray();
        }
    }

    /// Бинарный блок данных сигнатур
    public byte[] createSignaturesBinary(List<Signature> signatures) throws IOException {
        try (ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(dataStream)) {

            for (Signature signature : signatures) {
                SignatureBinaryDto dto = convertToBinaryDto(signature); // Сериализация в dto
                writeSignatureToStream(dataOutput, dto); // Кидаем сериализованный dto в поток
            }

            return dataStream.toByteArray();
        }
    }

    /// Сериализация в ДТО
    private SignatureBinaryDto convertToBinaryDto(Signature signature) {
        return SignatureBinaryDto.builder()
                .id(signature.getId())
                .threatName(signature.getThreatName())
                .firstBytes(signature.getFirstBytes())
                .remainderHash(signature.getRemainderHash())
                .remainderLength(signature.getRemainderLength())
                .fileType(signature.getFileType())
                .offsetStart(signature.getOffsetStart())
                .offsetEnd(signature.getOffsetEnd())
                .build();
    }

    /// Запись ДТО в бинарный поток
    private void writeSignatureToStream(DataOutputStream output, SignatureBinaryDto dto) throws IOException {
        output.writeUTF(dto.getId().toString());
        output.writeUTF(dto.getThreatName());
        output.write(dto.getFirstBytes());
        output.writeUTF(dto.getRemainderHash());
        output.writeInt(dto.getRemainderLength());
        output.writeUTF(dto.getFileType());
        output.writeInt(dto.getOffsetStart());
        output.writeInt(dto.getOffsetEnd());
    }

    /// Вычисление хэша
    private byte[] calculateHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    /// Подпись
    private byte[] signData(byte[] hash) throws Exception {
        java.security.Signature signature = java.security.Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
    }
}
