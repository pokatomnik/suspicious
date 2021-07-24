package tk.pokatomnik.suspicious.utils.encryption;

public interface TextEncryption {
    String encrypt(String strToEncrypt);
    String decrypt(String strToDecrypt);
    void notifyMasterKeyChanged(String masterKey);
}
