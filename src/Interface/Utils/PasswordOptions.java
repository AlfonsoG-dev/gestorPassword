package Interface.Utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public final record PasswordOptions(int size, boolean addLetter, boolean addSimbol, boolean addNumber) {

    private final static SecretKey getKey(String algorithm) {
        try {
            KeyGenerator key = KeyGenerator.getInstance("AES");
            key.init(128);
            return key.generateKey();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private final static IvParameterSpec generateIV() {
        byte[] initIV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(initIV);
        return new IvParameterSpec(initIV);
    }

    public final static String dataEncryption(String algorithm, String transformation, String input) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(algorithm), generateIV());
            byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return new String(encryptedBytes);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public final static String dataDecryption(String algorithm, String transformation, String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, getKey(algorithm), generateIV());
            byte[] plainText = cipher.doFinal(encryptedText.getBytes(StandardCharsets.UTF_8));
            return new String(plainText);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
