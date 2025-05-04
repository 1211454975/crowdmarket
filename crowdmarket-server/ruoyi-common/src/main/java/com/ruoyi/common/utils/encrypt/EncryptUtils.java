package com.ruoyi.common.utils.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加密工具类
 *
 * @author ruoyi
 */
public class EncryptUtils {
    private static final Logger log = LoggerFactory.getLogger(EncryptUtils.class);

    /**
     * 默认加密秘钥
     */
    private static final String DEFAULT_KEY = "RuoYiMetadataKey";

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 加密/解密算法/工作模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @return 加密后的数据
     */
    public static String encryptAES(String data) {
        return encryptAES(data, DEFAULT_KEY);
    }

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @param key 加密密钥
     * @return 加密后的数据
     */
    public static String encryptAES(String data, String key) {
        if (data == null || key == null) {
            return null;
        }
        try {
            // 创建密钥
            Key secretKey = new SecretKeySpec(getKey(key), ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            // 初始化为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 加密
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // Base64编码
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            return null;
        }
    }

    /**
     * AES解密
     *
     * @param encryptedData 加密数据
     * @return 解密后的数据
     */
    public static String decryptAES(String encryptedData) {
        return decryptAES(encryptedData, DEFAULT_KEY);
    }

    /**
     * AES解密
     *
     * @param encryptedData 加密数据
     * @param key 解密密钥
     * @return 解密后的数据
     */
    public static String decryptAES(String encryptedData, String key) {
        if (encryptedData == null || key == null) {
            return null;
        }
        try {
            // 创建密钥
            Key secretKey = new SecretKeySpec(getKey(key), ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            // 初始化为解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // Base64解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            // 解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            // 返回解密后的数据
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            return null;
        }
    }

    /**
     * 生成加密密钥
     *
     * @param key 密钥字符串
     * @return 密钥
     */
    private static byte[] getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[16]; // AES密钥长度为128位（16字节）

        // 如果密钥长度不足16字节，则重复填充
        if (keyBytes.length < 16) {
            System.arraycopy(keyBytes, 0, result, 0, keyBytes.length);
            // 填充剩余部分
            for (int i = keyBytes.length; i < 16; i++) {
                result[i] = keyBytes[i % keyBytes.length];
            }
        } else {
            // 如果密钥长度超过16字节，则截取前16字节
            System.arraycopy(keyBytes, 0, result, 0, 16);
        }

        return result;
    }
}