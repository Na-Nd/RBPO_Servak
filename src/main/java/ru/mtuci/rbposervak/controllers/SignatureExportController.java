package ru.mtuci.rbposervak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import ru.mtuci.rbposervak.services.SignatureExportService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/signatures/export")
public class SignatureExportController {

    private final SignatureExportService exportService;

    @Autowired
    public SignatureExportController(SignatureExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping(produces = MediaType.MULTIPART_MIXED_VALUE)
    public ResponseEntity<MultiValueMap<String, Object>> exportAll() throws Exception {
        return exportService.exportSignatures(null);
    }

    @PostMapping(produces = MediaType.MULTIPART_MIXED_VALUE)
    public ResponseEntity<MultiValueMap<String, Object>> exportSelected(
            @RequestBody List<UUID> signatureIds) throws Exception {
        return exportService.exportSignatures(signatureIds);
    }
}

