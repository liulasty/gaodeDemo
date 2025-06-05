package com.example.demo.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    // 加密方法
    public static String encrypt(String plainText, String key, String ivBase64) throws Exception {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        byte[] dataBytes = plainText.getBytes("UTF-8");

        // ZeroPadding
        int blockSize = 16;
        int paddedLength = ((dataBytes.length + blockSize - 1) / blockSize) * blockSize;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(dataBytes, 0, padded, 0, dataBytes.length);
        // 其余字节默认为 0

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(padded);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * 解密方法
     * @param encryptedBase64
     * @param key
     * @param ivBase64
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptedBase64, String key, String ivBase64) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encryptedBytes);

        // 去除结尾的零（模拟 ZeroPadding）
        int i = decrypted.length;
        while (i > 0 && decrypted[i - 1] == 0) {
            i--;
        }
        return new String(decrypted, 0, i, "UTF-8");
    }
}
