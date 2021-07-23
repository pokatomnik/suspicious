package tk.pokatomnik.suspicious.storage;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import tk.pokatomnik.suspicious.entities.Password;
import tk.pokatomnik.suspicious.utils.encryption.TextEncryption;

public class PasswordDatabaseService {
    private final PasswordDatabase passwordDatabase;

    private final Supplier<TextEncryption> getEncryption;

    public PasswordDatabaseService(
        PasswordDatabase initialPasswordDatabase,
        Supplier<TextEncryption> initialGetEncryption
    ) {
        passwordDatabase = initialPasswordDatabase;
        getEncryption = initialGetEncryption;
    }

    private Password encryptPassword(Password password) {
        final TextEncryption encryption = getEncryption.get();
        final Password newPassword = new Password(
            encryption.encrypt(password.getDomain()),
            encryption.encrypt(password.getUserName()),
            encryption.encrypt(password.getPassword()),
            encryption.encrypt(password.getComment())
        );
        newPassword.setUid(password.getUid());
        return newPassword;
    }

    private Password decryptPassword(Password password) {
        final TextEncryption encryption = getEncryption.get();
        final Password newPassword = new Password(
            encryption.decrypt(password.getDomain()),
            encryption.decrypt(password.getUserName()),
            encryption.decrypt(password.getPassword()),
            encryption.decrypt(password.getComment())
        );
        newPassword.setUid(password.getUid());
        return newPassword;
    }

    public Single<List<Password>> getAll() {
        return passwordDatabase.passwordDAO().getAll().map((passwords -> {
            return passwords.stream().map(this::decryptPassword).collect(Collectors.toList());
        }));
    };

    public Single<Password> getByUID(int uid) {
        return passwordDatabase.passwordDAO().getByUID(uid).map((this::decryptPassword));
    }

    public Completable insert(Password... passwords) {
        final Password[] encryptedPasswords = Arrays
            .stream(passwords)
            .map(this::encryptPassword)
            .toArray(Password[]::new);
        return passwordDatabase.passwordDAO().insert(encryptedPasswords);
    }

    public Completable delete(Password password) {
        final Password encryptedPassword = encryptPassword(password);
        return passwordDatabase.passwordDAO().delete(encryptedPassword);
    };

    public Completable update(Password password) {
        final Password encryptedPassword = encryptPassword(password);
        return passwordDatabase.passwordDAO().update(encryptedPassword);
    }
}
