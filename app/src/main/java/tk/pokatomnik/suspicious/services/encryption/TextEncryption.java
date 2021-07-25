package tk.pokatomnik.suspicious.services.encryption;

public interface TextEncryption {
    String encrypt(String strToEncrypt);
    String decrypt(String strToDecrypt);
    void notifyMasterKeyChanged(String masterKey);
}
