package com.example.demo.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class CryptoUtils {

    // 读取txt文件里的字符串
    public static String readFile(String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    /**
     * AES解密方法
     * @param encryptedData Base64编码的加密数据
     * @param key 密钥
     * @param iv Base64编码的IV
     * @return 解密后的字符串
     */
    public static String decrypt(String encryptedData, String key, String iv) throws Exception {
        byte[] ciphertext = Base64.getDecoder().decode(encryptedData);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] keyBytes = key.getBytes("UTF-8");

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decrypted = cipher.doFinal(ciphertext);
        return new String(decrypted, "UTF-8").trim();
    }

    /**
     * AES加密方法
     * @param plainText 明文
     * @param key 密钥
     * @param ivBase64 Base64编码的IV
     * @return Base64编码的加密数据
     */
    public static String encrypt(String plainText, String key, String ivBase64) throws Exception {
        byte[] keyBytes = key.getBytes("UTF-8");
        byte[] ivBytes = Base64.getDecoder().decode(ivBase64);
        byte[] plainData = plainText.getBytes("UTF-8");

        // 确保数据长度是16的倍数（AES块大小）
        int blockSize = 16;
        int paddingLength = blockSize - (plainData.length % blockSize);
        if (paddingLength < blockSize) {
            byte[] paddedData = new byte[plainData.length + paddingLength];
            System.arraycopy(plainData, 0, paddedData, 0, plainData.length);
            plainData = paddedData;
        }

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(plainData);
        return Base64.getEncoder().encodeToString(encrypted);
    }


    public static void main(String[] args) throws Exception {
        String key = "encryptedDatastr"; // 16 bytes for AES-128
        String iv = "nZX6BpoYCBuCRclhQo0zBw==";  // 16 bytes IV
        String original = readFile("D:\\soft\\gaode-demo1\\src\\main\\java\\com\\example\\demo\\util\\orign.txt");

        String encrypted = encrypt(original, key, iv);
        System.out.println("Encrypted: " + encrypted);

        String data  = readFile("D:\\soft\\gaode-demo1\\src\\main\\java\\com\\example\\demo\\util\\data.txt");

        List<StringDiff.DiffBlock> differences = StringDiff.findDifferences(data, encrypted);
        for (StringDiff.DiffBlock difference : differences) {
            System.out.printf("Position %d-%d: '%s' vs '%s'\n",
                    difference.getStart(), difference.getEnd(),
                    difference.getText1(), difference.getText2());
        }

        String decrypted = decrypt(encrypted,  key, iv);
        System.out.println("Decrypted: " + decrypted);
    }
//    public static void main(String[] args) throws Exception {
//        String key = "encryptedDatastr";
//        String iv = "nZX6BpoYCBuCRclhQo0zBw==";
//        String original = readFile("D:\\soft\\gaode-demo1\\src\\main\\java\\com\\example\\demo\\util\\orign.txt");
//
//        String encrypted = encrypt(key, iv, original);
//        System.out.println("Encrypted: " + encrypted);
//
//        String decrypted = decrypt(key, iv, encrypted);
//        System.out.println("Decrypted: " + decrypted);
//    }

}
