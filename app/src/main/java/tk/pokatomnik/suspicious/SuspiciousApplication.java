package tk.pokatomnik.suspicious;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import tk.pokatomnik.suspicious.storage.PasswordDatabase;
import tk.pokatomnik.suspicious.storage.PasswordDatabaseService;
import tk.pokatomnik.suspicious.utils.encryption.BlowfishEncryption;
import tk.pokatomnik.suspicious.utils.encryption.TextEncryption;

public class SuspiciousApplication extends Application {
    private PasswordDatabaseService passwordDatabaseService;

    private final TextEncryption encryption = new BlowfishEncryption();

    public PasswordDatabaseService getPasswordDatabaseService() {
        if (passwordDatabaseService == null) {
            final Context applicationContext = getApplicationContext();

            if (applicationContext == null) {
                throw new NullPointerException("getPasswordDatabase called too early: application isn't initialized yet");
            }

            final PasswordDatabase passwordDatabase = Room
                .databaseBuilder(getApplicationContext(), PasswordDatabase.class, "passwords.db")
                .build();
            passwordDatabaseService = new PasswordDatabaseService(passwordDatabase, this::getEncryptionService);
        }

        return passwordDatabaseService;
    }

    public TextEncryption getEncryptionService() {
        return encryption;
    }
}
