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

    public static class EncryptionException extends Exception {

        private static final long serialVersionUID = 503527060812817946L;

        public EncryptionException(Throwable thr) {
            super(thr);
        }
    }

    public static String encrypt(String string) throws EncryptionException {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        try {
            byte[] encrypted = cipher.doFinal(string.getBytes(Util.UTF8));
            return Base64.encodeToString(encrypted, false);
        } catch (BadPaddingException exc) {
            throw new EncryptionException(exc);
        } catch (IllegalBlockSizeException exc) {
            throw new EncryptionException(exc);
        } catch (UnsupportedEncodingException exc) {
            throw new EncryptionException(exc);
        }
    }

    public static String decrypt(String encrypted) throws EncryptionException {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        try {
            byte[] decrypted = cipher.doFinal(Base64.decode(encrypted));
            return new String(decrypted, Util.UTF8);
        } catch (IllegalBlockSizeException exc) {
            throw new EncryptionException(exc);
        } catch (BadPaddingException exc) {
            throw new EncryptionException(exc);
        } catch (UnsupportedEncodingException exc) {
            throw new EncryptionException(exc);
        }
    }

    private static Cipher getCipher(int mode) throws EncryptionException {
        try {
            return getUnsafeCipher(mode);
        } catch (InvalidKeySpecException exc) {
            throw new EncryptionException(exc);
        } catch (NoSuchAlgorithmException exc) {
            throw new EncryptionException(exc);
        } catch (NoSuchPaddingException exc) {
            throw new EncryptionException(exc);
        } catch (InvalidKeyException exc) {
            throw new EncryptionException(exc);
        } catch (InvalidAlgorithmParameterException exc) {
            throw new EncryptionException(exc);
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
