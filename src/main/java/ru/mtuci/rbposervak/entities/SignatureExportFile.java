package ru.mtuci.rbposervak.entities;

import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

@AllArgsConstructor
public class SignatureExportFile implements MultipartFile {

    private final byte[] manifestData; // Данные манифеста

    private final byte[] signaturesData; // Бинари сигнатур

    private final String filename;


    @Override
    public String getName() {
        return "signature_export";
    }

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public String getContentType() {
        return "multipart/mixed";
    }

    @Override
    public boolean isEmpty() {
        return manifestData.length == 0 && signaturesData.length == 0;
    }

    @Override
    public long getSize() {
        return manifestData.length + signaturesData.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write("--MANIFEST--\n".getBytes());
        output.write(manifestData);
        output.write("\n--DATA--\n".getBytes());
        output.write(signaturesData);
        return output.toByteArray();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getBytes());
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), getBytes());
    }

}
