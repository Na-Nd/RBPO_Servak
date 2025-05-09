package ru.mtuci.rbposervak.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mtuci.rbposervak.entities.ENUMS.signature.STATUS;
import ru.mtuci.rbposervak.entities.Signature;
import ru.mtuci.rbposervak.entities.SignatureExportFile;
import ru.mtuci.rbposervak.repositories.SignatureRepository;
import ru.mtuci.rbposervak.utils.SignatureExportUtil;


import java.util.List;
import java.util.UUID;

@Service
public class SignatureExportService {
    private final SignatureRepository signatureRepository;
    private final SignatureExportUtil signatureExportUtil;

    @Autowired
    public SignatureExportService(SignatureRepository signatureRepository, SignatureExportUtil signatureExportUtil) {
        this.signatureRepository = signatureRepository;
        this.signatureExportUtil = signatureExportUtil;
    }

    /// Экспорт всех сигнатрур
    public MultipartFile exportSignatures() throws Exception {
        List<Signature> actualSignatures = signatureRepository.findByStatus(STATUS.ACTUAL)
                .orElseThrow(() -> new RuntimeException("Actual signatures not found"));

        return exportFile(actualSignatures);
    }

    /// Экспорт по списку идшников
    public MultipartFile exportSignaturesByIds(List<UUID> ids) throws Exception {
        List<Signature> signatures = signatureRepository.findActualByIds(ids);

        if(signatures.isEmpty()) {
            throw new RuntimeException("Actual signatures not found");
        }

        return exportFile(signatures);
    }

    /// Создание самого экспорта
    private MultipartFile exportFile(List<Signature> signatures) throws Exception {
        byte[] manifestBytes = signatureExportUtil.createManifest(signatures); // манифест
        byte[] signaturesBytes = signatureExportUtil.createSignaturesBinary(signatures); // бинари сигнатур

        // Экспорт
        return new SignatureExportFile(
                manifestBytes,
                signaturesBytes,
                "signature_export_" + System.currentTimeMillis() + ".dat"
        );
    }


}