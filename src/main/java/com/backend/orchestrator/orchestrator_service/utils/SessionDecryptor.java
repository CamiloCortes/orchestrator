package com.backend.orchestrator.orchestrator_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;

@Component
public class SessionDecryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH_BITS = 128;

    @Value("${session.encryption-key}")
    private String encryptionKeyHex;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }

        try {
            String[] parts = encryptedData.split(":");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Formato de sesión cifrada inválido");
            }

            byte[] iv = HexFormat.of().parseHex(parts[0]);
            byte[] authTag = HexFormat.of().parseHex(parts[1]);
            byte[] ciphertext = HexFormat.of().parseHex(parts[2]);
            byte[] ciphertextWithTag = new byte[ciphertext.length + authTag.length];
            System.arraycopy(ciphertext, 0, ciphertextWithTag, 0, ciphertext.length);
            System.arraycopy(authTag, 0, ciphertextWithTag, ciphertext.length, authTag.length);

            byte[] keyBytes = HexFormat.of().parseHex(encryptionKeyHex);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] plainBytes = cipher.doFinal(ciphertextWithTag);
            String json = new String(plainBytes, StandardCharsets.UTF_8);

            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar sesión", e);
        }
    }
}
