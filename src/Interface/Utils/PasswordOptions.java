package Interface.Utils;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final record PasswordOptions(int size, boolean addLetter, boolean addSimbol, boolean addNumber) {

    private final static String SALT = "12345678";

    private final static String ALGORITM = "AES/CBC/PKCS5Padding";

    public final static SecretKey getKeyFromPassword(String password, String salt) {
        SecretKey key = null;
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
            key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return key;
    }
    public final static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
    public final static String ecryptPassword(String algoritm, String inputText, SecretKey key, IvParameterSpec iv) {
        String encrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(algoritm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] cipherText = cipher.doFinal(inputText.getBytes());
            encrypted = Base64.getEncoder().encodeToString(cipherText);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }
    public final static String decryptPassword(String algoritm, String cipherText, SecretKey key, IvParameterSpec iv) {
        String dencrypted = null;
        try {
            Cipher cipher = Cipher.getInstance(algoritm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] painText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            dencrypted = new String(painText);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return dencrypted;
    }
    public static void TestPassword() {
        try {
            String input = "say my name";
            String salt = SALT;
            IvParameterSpec iv = generateIV();
            SecretKey key = getKeyFromPassword(input, salt);
            String algorithm = ALGORITM;
            String cipherText = ecryptPassword(algorithm, input, key, iv);
            String plainText = decryptPassword(algorithm, cipherText, key, iv);
            if(input.equals(plainText)) {
                System.out.println("input and decrypt text are equals");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
