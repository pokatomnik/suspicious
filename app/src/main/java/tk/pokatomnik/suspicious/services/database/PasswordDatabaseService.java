package tk.pokatomnik.suspicious.services.database;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.room.Room;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import tk.pokatomnik.suspicious.services.database.entities.Password;

public class PasswordDatabaseService {
    @Nullable
    private PasswordDatabase pdb = null;

    private final PasswordDatabaseServiceDependencies deps;

    public PasswordDatabaseService(PasswordDatabaseServiceDependencies initialDeps) {
        deps = initialDeps;
    }

    private PasswordDatabase getPasswordDatabase() {
        if (pdb != null) {
            return pdb;
        }

        final Context context = deps.getContext();

        if (context == null) {
            throw new NullPointerException("getPasswordDatabase called too early: application isn't initialized yet");
        }

        pdb = Room.databaseBuilder(context, PasswordDatabase.class, "passwords.db").build();

        return pdb;
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
        return getPasswordDatabase().passwordDAO().getAll().map((passwords -> {
            return passwords.stream().map(this::decryptPassword).collect(Collectors.toList());
        }));
    }

    public Single<Password> getByUID(int uid) {
        return getPasswordDatabase().passwordDAO().getByUID(uid).map((this::decryptPassword));
    }

    public Completable insert(Password... passwords) {
        final Password[] encryptedPasswords = Arrays
            .stream(passwords)
            .map(this::encryptPassword)
            .toArray(Password[]::new);
        return getPasswordDatabase().passwordDAO().insert(encryptedPasswords);
    }

    public Completable delete(Password password) {
        final Password encryptedPassword = encryptPassword(password);
        return getPasswordDatabase().passwordDAO().delete(encryptedPassword);
    }

    public Completable update(Password password) {
        final Password encryptedPassword = encryptPassword(password);
        return getPasswordDatabase().passwordDAO().update(encryptedPassword);
    }
}
