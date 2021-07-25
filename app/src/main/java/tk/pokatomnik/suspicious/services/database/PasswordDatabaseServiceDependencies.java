package tk.pokatomnik.suspicious.services.database;

import java.util.function.Function;

public class PasswordDatabaseServiceDependencies {
    private final Function<String, String> encrypt;

    private final Function<String, String> decrypt;

    public PasswordDatabaseServiceDependencies(
        Function<String, String> initialEncrypt,
        Function<String, String> initialDecrypt
    ) {
        encrypt = initialEncrypt;
        decrypt = initialDecrypt;
    }

    public String encrypt(String value) {
        return encrypt.apply(value);
    }

    public String decrypt(String value) {
        return decrypt.apply(value);
    }
}
