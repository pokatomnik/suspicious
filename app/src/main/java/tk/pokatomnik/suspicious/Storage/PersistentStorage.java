package tk.pokatomnik.suspicious.Storage;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.room.Room;

public final class PersistentStorage {
    private static volatile PersistentStorage instance;

    private final PasswordDatabase passwordDatabase;

    private PersistentStorage(Context context) {
        passwordDatabase = Room
            .databaseBuilder(context, PasswordDatabase.class, "passwords")
            .build();
    }

    public static PersistentStorage getInstance(@Nullable Context context) {
        PersistentStorage localInstance = instance;
        if (localInstance == null) {
            synchronized (PersistentStorage.class) {
                localInstance = instance;
                if (localInstance == null) {
                    if (context == null) {
                        throw new IllegalArgumentException("Context isn't initialized yet, provide application context to do lazy initialization");
                    }
                    instance = localInstance = new PersistentStorage(context);
                }
            }
        }
        return localInstance;
    }

    public PasswordDatabase getPasswordDatabase() {
        return passwordDatabase;
    }
}
