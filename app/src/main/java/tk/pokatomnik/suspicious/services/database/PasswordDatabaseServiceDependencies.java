package tk.pokatomnik.suspicious.services.database;

import android.content.Context;

import java.util.function.Function;
import java.util.function.Supplier;

public class PasswordDatabaseServiceDependencies {
    private final Function<String, String> encrypt;

    private final Function<String, String> decrypt;

    private final Supplier<Context> getContext;

    public PasswordDatabaseServiceDependencies(
        Supplier<Context> initialGetContext,
        Function<String, String> initialEncrypt,
        Function<String, String> initialDecrypt
    ) {
        getContext = initialGetContext;
        encrypt = initialEncrypt;
        decrypt = initialDecrypt;
    }

    public String encrypt(String value) {
        return encrypt.apply(value);
    }

    public String decrypt(String value) {
        return decrypt.apply(value);
    }

    public Context getContext() {
        return getContext.get();
    }
}
