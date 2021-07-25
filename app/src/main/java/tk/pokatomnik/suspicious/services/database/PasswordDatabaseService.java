package tk.pokatomnik.suspicious.services.database;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import tk.pokatomnik.suspicious.services.database.entities.Password;

public class PasswordDatabaseService {
    private final PasswordDatabase passwordDatabase;

    private final PasswordDatabaseServiceDependencies deps;

    public PasswordDatabaseService(
        PasswordDatabase initialPasswordDatabase,
        PasswordDatabaseServiceDependencies initialDeps
    ) {
        passwordDatabase = initialPasswordDatabase;
        deps = initialDeps;
    }

    private Password encryptPassword(Password password) {
        final Password newPassword = new Password(
            deps.encrypt(password.getDomain()),
            deps.encrypt(password.getUserName()),
            deps.encrypt(password.getPassword()),
            deps.encrypt(password.getComment())
        );
        newPassword.setUid(password.getUid());
        return newPassword;
    }

    private Password decryptPassword(Password password) {
        final Password newPassword = new Password(
            deps.decrypt(password.getDomain()),
            deps.decrypt(password.getUserName()),
            deps.decrypt(password.getPassword()),
            deps.decrypt(password.getComment())
        );
        newPassword.setUid(password.getUid());
        return newPassword;
    }

    public Single<List<Password>> getAll() {
        return passwordDatabase.passwordDAO().getAll().map((passwords -> {
            return passwords.stream().map(this::decryptPassword).collect(Collectors.toList());
        }));
    }

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
    }

    public Completable update(Password password) {
        final Password encryptedPassword = encryptPassword(password);
        return passwordDatabase.passwordDAO().update(encryptedPassword);
    }
}
