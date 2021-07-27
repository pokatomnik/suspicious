package tk.pokatomnik.suspicious.services.export;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.pokatomnik.suspicious.services.database.entities.Password;

public class PasswordsExportImport {
    private final ExportDependencies deps;

    public PasswordsExportImport(ExportDependencies initialExportDependencies) {
        deps = initialExportDependencies;
    }

    private Disposable importPasswords(List<Password> passwords, BiConsumer<String, Integer> displayToastInUIThread) {
        if (passwords.size() == 0) {
            displayToastInUIThread.accept("No passwords to import", Toast.LENGTH_LONG);
            return Disposable.empty();
        }

        final Password[] passwordsArray = passwords.toArray(new Password[0]);
        return deps.insert(passwordsArray).subscribeOn(Schedulers.single()).subscribe(() -> {
            displayToastInUIThread.accept("Import finished", Toast.LENGTH_LONG);
        }, (e) -> {
            displayToastInUIThread.accept("Import failed", Toast.LENGTH_LONG);
        });
    }

    public Disposable importPasswords(
        @Nullable Uri fileURI,
        @Nullable Activity activity,
        BiConsumer<String, Integer> displayToastInUIThread
    ) {
        final String importErrorMessage = "Error importing file";
        final String importCancelledMessage = "Import cancelled";

        if (fileURI == null) {
            displayToastInUIThread.accept(importCancelledMessage, Toast.LENGTH_LONG);
            return Disposable.empty();
        }

        if (activity == null) {
            displayToastInUIThread.accept(importErrorMessage, Toast.LENGTH_SHORT);
            return Disposable.empty();
        }

        try (InputStream is = activity.getContentResolver().openInputStream(fileURI)) {
            final InputStreamReader inputStreamReader = new InputStreamReader(is);
            final Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
            final String data = scanner.hasNext() ? scanner.next() : "";
            final List<Password> passwords = new CSVImporter().importFromString(data);

            return importPasswords(passwords, displayToastInUIThread);
        } catch (IOException e) {
            displayToastInUIThread.accept(importErrorMessage, Toast.LENGTH_LONG);
            return Disposable.empty();
        }
    }

    public Disposable exportPasswords(
        @Nullable Uri fileURI,
        @Nullable Activity activity,
        BiConsumer<String, Integer> displayToastInUIThread
    ) {
        final String exportErrorMessage = "Error exporting file";
        final String exportCancelledMessage = "Export cancelled";

        if (fileURI == null) {
            displayToastInUIThread.accept(exportCancelledMessage, Toast.LENGTH_SHORT);
            return Disposable.empty();
        }

        if (activity == null) {
            displayToastInUIThread.accept(exportErrorMessage, Toast.LENGTH_LONG);
            return Disposable.empty();
        }

        final CSVExporter csvExporter = new CSVExporter();
        return deps.getAll().map(csvExporter::exportToString).subscribeOn(Schedulers.single()).subscribe((result) -> {
            try (OutputStream os = activity.getContentResolver().openOutputStream(fileURI)) {
                if (os == null) {
                    throw new IOException("Failed to initialize Output stream");
                }
                os.write(result.getBytes());
                os.close();
                displayToastInUIThread.accept("Exported successfully", Toast.LENGTH_SHORT);
            }
            catch(IOException e) {
                displayToastInUIThread.accept(exportErrorMessage, Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        });
    }
}
