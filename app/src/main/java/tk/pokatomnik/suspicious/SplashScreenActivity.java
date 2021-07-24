package tk.pokatomnik.suspicious;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;
import java.util.function.Consumer;

import tk.pokatomnik.suspicious.ui.settings.SettingsStore;
import tk.pokatomnik.suspicious.utils.inputdialog.NewPasswordInputDialog;
import tk.pokatomnik.suspicious.utils.inputdialog.PasswordInputDialog;
import tk.pokatomnik.suspicious.utils.MD5;
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
        askNewMasterPasswordUntilCorrect((newMasterKey) -> {
            final String masterKeyHash = new MD5(newMasterKey).toString();
            settingsStore.setMasterPasswordHash(masterKeyHash);
            passwordDatabaseService().notifyMasterKeyChanged(newMasterKey);
            startMainActivity();
        });
    }

    private void askNewMasterPasswordUntilCorrect(Consumer<String> consumer) {
        final String dialogTitle = "Set up master password";
        new NewPasswordInputDialog(dialogTitle, this).ask((error, newMasterKey) -> {
            if (error == null) {
                consumer.accept(newMasterKey);
            } else {
                final String errorText = error.getMessage();
                Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
                askNewMasterPasswordUntilCorrect(consumer);
            }
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
        new PasswordInputDialog(dialogTitle, this).ask((error, masterKey) -> {
            final String possibleHash = new MD5(masterKey).toString();
            if (existingHash.equals(possibleHash)) {
                consumer.accept(masterKey);
            } else {
                final String errorText = Optional.ofNullable(error).map(Throwable::getMessage)
                    .orElse("Incorrect master password");
                Toast.makeText(this, errorText, Toast.LENGTH_LONG).show();
                askMasterPasswordUtilCorrect(existingHash, consumer);
            }
        });
    }
}