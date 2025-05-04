package com.ruoyi.tenant.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class AESUtil {
    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_NAME = "AES";
    private static final String ALGORITHM = "AES/CBC/PKCS7Padding";
    private static final String IV = "6agrioBE1D9yoGOX"; // 初始化向量

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String encrypt(String content, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(CHARSET_NAME));
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(content.getBytes(CHARSET_NAME));
        return Base64.encodeBase64String(encrypted);
    }

    public static String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec("6agrioBE1D9yoGOX".getBytes(CHARSET_NAME), AES_NAME);
        IvParameterSpec ivParameterSpec = new IvParameterSpec("6agrioBE1D9yoGOX".getBytes(CHARSET_NAME));
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decodedBytes = Base64.decodeBase64(encrypted);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, CHARSET_NAME);
    }
}