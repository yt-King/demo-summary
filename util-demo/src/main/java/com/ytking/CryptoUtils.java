package com.ytking;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @author yt
 * @package: com.ytking
 * @className: EncryptDecryptUtil
 * @date 2023/10/11
 * @description: 加解密工具类
 */

public class CryptoUtils {

    public static String decrypt(String encrypted, String key, String iv) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = Base64.decodeBase64(encrypted);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String encrypt(String input, String key, String iv) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = iv.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(ivBytes));

        byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(encryptedBytes);
    }

    public static void main(String[] args) throws Exception {
        String key = "1122334455667788";
        String iv = "1122334455667788";
        String textToEncrypt = "Hello, world!";

        String encryptedText = encrypt(textToEncrypt, key, iv);
        System.out.println("Encrypted: " + encryptedText);

        String decryptedText = decrypt(encryptedText, key, iv);
        System.out.println("Decrypted: " + decryptedText);
    }
}
