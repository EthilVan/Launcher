package fr.ethilvan.launcher.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public final class Encryption {

    public static String encrypt(String string) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        try {
            byte[] encrypted = cipher.doFinal(string.getBytes(Util.UTF8));
            return Base64.encodeToString(encrypted, false);
        } catch (BadPaddingException exc) {
            throw Util.wrap(exc);
        } catch (IllegalBlockSizeException exc) {
            throw Util.wrap(exc);
        } catch (UnsupportedEncodingException exc) {
            throw Util.wrap(exc);
        }
    }

    public static String decrypt(String encrypted) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        try {
            byte[] decrypted = cipher.doFinal(Base64.decode(encrypted));
            return new String(decrypted, Util.UTF8);
        } catch (IllegalBlockSizeException exc) {
            throw Util.wrap(exc);
        } catch (BadPaddingException exc) {
            throw Util.wrap(exc);
        } catch (UnsupportedEncodingException exc) {
            throw Util.wrap(exc);
        }
    }

    private static Cipher getCipher(int mode) {
        try {
            return getUnsafeCipher(mode);
        } catch (InvalidKeySpecException exc) {
            throw Util.wrap(exc);
        } catch (NoSuchAlgorithmException exc) {
            throw Util.wrap(exc);
        } catch (NoSuchPaddingException exc) {
            throw Util.wrap(exc);
        } catch (InvalidKeyException exc) {
            throw Util.wrap(exc);
        } catch (InvalidAlgorithmParameterException exc) {
            throw Util.wrap(exc);
        }
    }

    private static Cipher getUnsafeCipher(int mode)
            throws InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        Random random = new Random(5598905L);
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10);
        SecretKey pbeKey;
        pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
                .generateSecret(new PBEKeySpec("aubedagnal".toCharArray()));
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(mode, pbeKey, pbeParamSpec);
        return cipher;
    }

    private Encryption() {
    }
}
