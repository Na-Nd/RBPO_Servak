package ru.mtuci.rbposervak.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.mtuci.rbposervak.entities.DTO.SignatureBinaryDto;
import ru.mtuci.rbposervak.entities.ENUMS.signature.STATUS;
import ru.mtuci.rbposervak.entities.Signature;
import ru.mtuci.rbposervak.repositories.SignatureRepository;
import ru.mtuci.rbposervak.utils.SignatureExportUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignatureExportService {
    private final SignatureRepository signatureRepository;

    /// Экспорт
    public ResponseEntity<MultiValueMap<String, Object>> exportSignatures(List<UUID> ids) throws Exception {
        List<Signature> signatures = ids == null || ids.isEmpty()
                ? signatureRepository.findByStatus(STATUS.ACTUAL)
                .orElseThrow(() -> new RuntimeException("No active signatures found"))
                : signatureRepository.findActualByIds(ids);

        if(signatures.isEmpty()) {
            throw new RuntimeException("No signatures found for export");
        }

        // Сериализация данных
        byte[] manifestBytes = serializeManifest(signatures);
        byte[] signaturesBytes = serializeSignatures(signatures);

        // Формирование ответа
        return buildMultipartResponse(manifestBytes, signaturesBytes);
    }

    /// Сериализация манифеста
    private byte[] serializeManifest(List<Signature> signatures) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Заголовок манифеста
        writeIntLE(baos, 0x4D414E46); // "MANF" magic number
        writeIntLE(baos, 1);          // Version
        writeIntLE(baos, signatures.size());

        // Записи о сигнатурах
        //тут id - имя а надо id - подпись
        for (Signature sig : signatures) {
            writeUuidLE(baos, sig.getId());
            writeStringLE(baos, sig.getDigitalSignature());
            baos.write(sig.getFirstBytes()); // Первые 8 байт сигнатуры
        }

        return baos.toByteArray();
    }

    /// Сериализация самих сигнатур
    private byte[] serializeSignatures(List<Signature> signatures) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Заголовок данных
        writeIntLE(baos, 0x5349474E); // "SIGN" magic number
        writeIntLE(baos, 1);           // Version
        writeIntLE(baos, signatures.size());

        // Данные сигнатур
        for (Signature sig : signatures) {
            SignatureBinaryDto dto = convertToBinaryDto(sig);
            writeSignatureData(baos, dto);
        }

        return baos.toByteArray();
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

    /// Запись данных сигнатуры в поток
    private void writeSignatureData(ByteArrayOutputStream baos, SignatureBinaryDto dto) throws IOException {
        // ID сигнатуры
        writeUuidLE(baos, dto.getId());

        // Название угрозы
        writeStringLE(baos, dto.getThreatName());

        // Первые 8 байт сигнатуры
        baos.write(dto.getFirstBytes());

        // Хэш остатка
        byte[] remainderHash = hexStringToByteArray(dto.getRemainderHash());
        writeIntLE(baos, remainderHash.length);
        baos.write(remainderHash);

        // Длина остатка
        writeIntLE(baos, dto.getRemainderLength());

        // Тип файла
        writeStringLE(baos, dto.getFileType());

        // Смещения
        writeIntLE(baos, dto.getOffsetStart());
        writeIntLE(baos, dto.getOffsetEnd());
    }

    /// Конвертация hex-строки в byte[]
    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    /// Формирование multipart ответа
    private ResponseEntity<MultiValueMap<String, Object>> buildMultipartResponse(
            byte[] manifest, byte[] data) {

        ByteArrayResource manifestRes = new ByteArrayResource(manifest) {
            @Override
            public String getFilename() {
                return "manifest.bin";
            }
        };

        ByteArrayResource dataRes = new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return "signatures.bin";
            }
        };

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("manifest", new HttpEntity<>(manifestRes, createHeaders("manifest.bin")));
        parts.add("signatures", new HttpEntity<>(dataRes, createHeaders("signatures.bin")));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("multipart/mixed"))
                .body(parts);
    }

    /// Вспомогательные методы сериализации
    private void writeUuidLE(ByteArrayOutputStream baos, UUID uuid) {
        writeLongLE(baos, uuid.getMostSignificantBits());
        writeLongLE(baos, uuid.getLeastSignificantBits());
    }

    private void writeLongLE(ByteArrayOutputStream baos, long value) {
        for (int i = 0; i < 8; i++) {
            baos.write((byte) (value >> (i * 8)));
        }
    }

    private void writeIntLE(ByteArrayOutputStream baos, int value) {
        for (int i = 0; i < 4; i++) {
            baos.write((byte) (value >> (i * 8)));
        }
    }

    private void writeStringLE(ByteArrayOutputStream baos, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeIntLE(baos, bytes.length);
        baos.write(bytes, 0, bytes.length);
    }

    private HttpHeaders createHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename)
                        .build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }
}