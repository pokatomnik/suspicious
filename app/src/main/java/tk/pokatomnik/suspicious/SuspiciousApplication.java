package tk.pokatomnik.suspicious;

import android.app.Application;

import tk.pokatomnik.suspicious.services.database.PasswordDatabaseService;
import tk.pokatomnik.suspicious.services.database.PasswordDatabaseServiceDependencies;
import tk.pokatomnik.suspicious.services.encryption.BlowfishEncryption;
import tk.pokatomnik.suspicious.services.encryption.TextEncryption;
import tk.pokatomnik.suspicious.services.settings.Settings;
import tk.pokatomnik.suspicious.services.settings.SettingsDependencies;
import tk.pokatomnik.suspicious.services.systeminfo.SystemInfo;
import tk.pokatomnik.suspicious.services.systeminfo.SystemInfoDependencies;

public class SuspiciousApplication extends Application {
    private final TextEncryption encryption = new BlowfishEncryption();

    private final Settings settings = new Settings(
        new SettingsDependencies(this::getApplicationContext)
    );

    private final PasswordDatabaseService passwordDatabaseService = new PasswordDatabaseService(
        new PasswordDatabaseServiceDependencies(
            this::getApplicationContext,
            encryption::encrypt,
            encryption::decrypt
        )
    );

    private final SystemInfo systemInfo = new SystemInfo(
        new SystemInfoDependencies(this::getApplicationContext)
    );


    public PasswordDatabaseService getPasswordDatabaseService() {
        return passwordDatabaseService;
    }

    public TextEncryption getEncryptionService() {
        return encryption;
    }

    public Settings getSettingsService() {
        return settings;
    }

    public SystemInfo getSystemInfoService() {
        return systemInfo;
    }
}
