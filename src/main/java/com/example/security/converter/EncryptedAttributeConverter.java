package com.example.security.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据库字段加密转换器
 * 
 * 注意：当前实现使用了硬编码的加密密钥，这在生产环境中是不安全的。
 * 在实际生产环境中，应该：
 * 1. 使用密钥管理服务（KMS）管理密钥
 * 2. 从安全的配置中心获取密钥
 * 3. 使用环境变量注入密钥
 * 4. 实现密钥轮换机制
 * 5. 考虑使用硬件安全模块（HSM）
 */
@Converter(autoApply = false)
public class EncryptedAttributeConverter implements AttributeConverter<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(EncryptedAttributeConverter.class);

    // 使用32字节(256位)的密钥
    private static final String ENCRYPTION_KEY = "12345678901234567890123456789012";
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private final SecretKey secretKey;

    public EncryptedAttributeConverter() {
        try {
            // 确保密钥长度正确
            byte[] keyBytes = ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException("加密密钥必须是32字节长度");
            }
            this.secretKey = new SecretKeySpec(keyBytes, "AES");

            // 验证密钥是否可用
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, this.secretKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH * 8, new byte[GCM_IV_LENGTH]));
        } catch (Exception e) {
            logger.error("初始化加密转换器失败: {}", e.getMessage(), e);
            throw new RuntimeException("初始化加密转换器失败", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            logger.debug("开始加密数据: {}", attribute);

            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);

            String result = Base64.getEncoder().encodeToString(combined);
            logger.debug("加密完成，加密后长度: {}", result.length());
            return result;
        } catch (Exception e) {
            logger.error("加密失败: {}", e.getMessage(), e);
            throw new RuntimeException("加密失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            logger.debug("开始解密数据，加密数据长度: {}", dbData.length());

            byte[] decoded = Base64.getDecoder().decode(dbData);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, GCM_IV_LENGTH);

            byte[] encryptedData = new byte[decoded.length - GCM_IV_LENGTH];
            System.arraycopy(decoded, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            String result = new String(decryptedData, StandardCharsets.UTF_8);
            logger.debug("解密完成");
            return result;
        } catch (Exception e) {
            logger.error("解密失败: {}", e.getMessage(), e);
            throw new RuntimeException("解密失败: " + e.getMessage(), e);
        }
    }
}