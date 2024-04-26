package utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

import javax.servlet.ServletConfig;

public class PasswordEncryption {
 
    private String keyValueStr;
    private byte[] keyValue;
    private String ALGORITHM;
    private Key key;
    private Cipher c;

    public PasswordEncryption(ServletConfig config) throws Exception {
        keyValueStr = config.getInitParameter("secretKey");
        keyValue = keyValueStr.getBytes();
        ALGORITHM = config.getInitParameter("cipher");
        key = generateKey(keyValue, ALGORITHM);
        c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
    }

    public String encrypt(String password) throws Exception {
        byte[] encValue = c.doFinal(password.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encValue);
        return encryptedValue;
    }

    private Key generateKey(byte[] keyValue, String ALGORITHM) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}
