package ru.mtuci.rbposervak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mtuci.rbposervak.services.SignatureExportService;

@RestController
@RequestMapping("/api/signatures")
public class SignatureExportController {
    private final SignatureExportService signatureExportService;

    @Autowired
    public SignatureExportController(SignatureExportService signatureExportService) {
        this.signatureExportService = signatureExportService;
    }

    @GetMapping(value = "/export", produces = "multipart/mixed")
    public ResponseEntity<Resource> exportSignatures() throws Exception {
        MultipartFile file = signatureExportService.exportSignatures();

        return ResponseEntity.status(200)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .contentLength(file.getSize())
                .body(new ByteArrayResource(file.getBytes()));
    }
}
