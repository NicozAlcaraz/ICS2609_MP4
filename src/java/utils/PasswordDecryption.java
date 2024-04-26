package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

import javax.servlet.ServletConfig;

public class PasswordDecryption {
 
    private String keyValueStr;
    private byte[] keyValue;
    private String ALGORITHM;
    private Key key;
    private Cipher c;

    public PasswordDecryption(ServletConfig config) throws Exception {
        keyValueStr = config.getInitParameter("secretKey");
        keyValue = keyValueStr.getBytes();
        ALGORITHM = config.getInitParameter("cipher");
        key = generateKey(keyValue, ALGORITHM);
        c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
    }

    public String decrypt(String encryptedPassword) throws Exception {
        byte[] decodedValue = Base64.getDecoder().decode(encryptedPassword);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private Key generateKey(byte[] keyValue, String ALGORITHM) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}
