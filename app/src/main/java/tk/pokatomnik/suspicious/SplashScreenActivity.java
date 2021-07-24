package tk.pokatomnik.suspicious;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;
import java.util.function.Consumer;

import tk.pokatomnik.suspicious.storage.PasswordDatabaseService;
import tk.pokatomnik.suspicious.ui.settings.SettingsStore;
import tk.pokatomnik.suspicious.utils.InputDialog;
import tk.pokatomnik.suspicious.utils.MD5;
import tk.pokatomnik.suspicious.utils.ToastError;
import tk.pokatomnik.suspicious.utils.encryption.TextEncryption;

public class SplashScreenActivity extends AppCompatActivity {
    private SettingsStore settingsStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsStore = new SettingsStore(getApplicationContext());
        final String masterPasswordHash = settingsStore.getMasterPasswordHash();

        if (masterPasswordHash == null) {
            handleFirstRun();
        } else {
            handleRun(masterPasswordHash);
        }
    }

    private TextEncryption passwordDatabaseService() {
        final SuspiciousApplication application = (SuspiciousApplication) getApplication();
        return application.getEncryptionService();
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }

    private void handleFirstRun() {
        final String dialogTitle = "Set up master password";
        new InputDialog(dialogTitle, this).ask((newMasterKey) -> {
            final String masterKeyHash = new MD5(newMasterKey).toString();
            settingsStore.setMasterPasswordHash(masterKeyHash);
            passwordDatabaseService().notifyMasterKeyChanged(newMasterKey);
            startMainActivity();
        });
    }

    private void handleRun(String existingMasterPasswordHash) {
        askMasterPasswordUtilCorrect(existingMasterPasswordHash, (validMasterPassword) -> {
            passwordDatabaseService().notifyMasterKeyChanged(validMasterPassword);
            startMainActivity();
        });
    }

    private void askMasterPasswordUtilCorrect(String existingHash, Consumer<String> consumer) {
        final String dialogTitle = "Specify master password";
        new InputDialog(dialogTitle, this).ask((masterKey) -> {
            final String possibleHash = new MD5(masterKey).toString();
            if (existingHash.equals(possibleHash)) {
                consumer.accept(masterKey);
            } else {
                Toast.makeText(this, "Incorrect master password", Toast.LENGTH_LONG).show();
                askMasterPasswordUtilCorrect(existingHash, consumer);
            }
        });
    }
}