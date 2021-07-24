package tk.pokatomnik.suspicious.utils.encryption;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BlowfishEncryption implements TextEncryption {
    private String key;

    private Cipher cipher;

    public BlowfishEncryption() {
        try {
            cipher = Cipher.getInstance("Blowfish");
        } catch (Exception ignored) {
            key = null;
            cipher = null;
        }
    }


    @Override
    public String encrypt(String strToEncrypt) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                "Blowfish")
            );

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
            final byte[] base64Bytes = Base64.getEncoder().encode(encrypted);
            return new String(base64Bytes, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return "";
        }
    }

    @Override
    public String decrypt(String strToDecrypt) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                "Blowfish")
            );
            byte[] decodedBytes = Base64.getDecoder().decode(strToDecrypt.getBytes(StandardCharsets.UTF_8));
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return "";
        }
    }

    @Override
    public void notifyMasterKeyChanged(String masterKey) {
        key = masterKey;
    }
}
