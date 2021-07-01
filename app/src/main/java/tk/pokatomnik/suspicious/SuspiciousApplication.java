package tk.pokatomnik.suspicious;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import tk.pokatomnik.suspicious.Storage.PasswordDatabase;

public class SuspiciousApplication extends Application {
    private PasswordDatabase passwordDatabase;

    public PasswordDatabase getPasswordDatabase() {
        if (passwordDatabase == null) {
            final Context applicationContext = getApplicationContext();

            if (applicationContext == null) {
                throw new NullPointerException("getPasswordDatabase called too early: application isn't initialized yet");
            }

            passwordDatabase = Room
                .databaseBuilder(getApplicationContext(), PasswordDatabase.class, "passwords")
                .build();
        }

        return passwordDatabase;
    }
}
