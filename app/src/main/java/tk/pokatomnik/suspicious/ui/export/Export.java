package tk.pokatomnik.suspicious.ui.export;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.databinding.FragmentExportBinding;

public class Export extends Fragment {
    private final static String MIME_CSV = "text/csv";

    private final static String MIME_COMMA_SEPARATED_VALUES = "text/comma-separated-values";

    private FragmentExportBinding binding;

    private final CSVExporter csvExporter = new CSVExporter();

    final ActivityResultLauncher<String> exportLauncher =
            registerForActivityResult(new ExportActivityResultContract(MIME_CSV), this::onExportFileCreated);

    final ActivityResultLauncher<String[]> importLauncher =
            registerForActivityResult(new ImportActivityResultContract(), this::onImportFile);

    @Nullable
    private Disposable exportSubscription;

    @Nullable
    private Disposable importSubscription;

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentExportBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
        @NonNull View view,
        @Nullable Bundle savedInstanceState
    ) {
        binding.exportCSV.setOnClickListener(this::handleExportCSV);
        binding.importCSV.setOnClickListener(this::handleImportCSV);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Optional.ofNullable(exportSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(importSubscription).ifPresent(Disposable::dispose);
    }

    private Single<List<Password>> getAllPasswords() {
        return Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            return application.getPasswordDatabase().passwordDAO().getAll().subscribeOn(Schedulers.single());
        }).orElse(Single.never());
    }

    private void handleExportCSV(View view) {
        exportLauncher.launch("suspicious.export.csv");
    }

    private void handleImportCSV(View view) {
        importLauncher.launch(new String[] { MIME_CSV, MIME_COMMA_SEPARATED_VALUES });
    }

    private void displayToastInUIThread(String message, int duration) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(() -> {
            Toast.makeText(getContext(), message, duration).show();
        });
    }

    private void importPasswords(List<Password> passwords) {
        if (passwords.size() == 0) {
            displayToastInUIThread("No passwords to import", Toast.LENGTH_LONG);
            return;
        }
        importSubscription = Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            final Password[] passwordsArray = passwords.toArray(new Password[0]);
            return application.getPasswordDatabase().passwordDAO().insert(passwordsArray);
        }).orElse(Completable.never()).subscribeOn(Schedulers.single()).subscribe(() -> {
            displayToastInUIThread("Import finished", Toast.LENGTH_LONG);
        }, (e) -> {
            displayToastInUIThread("Import failed", Toast.LENGTH_LONG);
        });
    }

    private void onImportFile(@Nullable Uri fileURI) {
        final Activity activity = getActivity();
        final String importErrorMessage = "Error importing file";
        final String importCancelledMessage = "Import cancelled";

        if (fileURI == null) {
            displayToastInUIThread(importCancelledMessage, Toast.LENGTH_LONG);
            return;
        }

        if (activity == null) {
            displayToastInUIThread(importErrorMessage, Toast.LENGTH_SHORT);
            return;
        }

        try (InputStream is = activity.getContentResolver().openInputStream(fileURI)) {
            final InputStreamReader inputStreamReader = new InputStreamReader(is);
            final Scanner scanner = new Scanner(inputStreamReader).useDelimiter("\\A");
            final String data = scanner.hasNext() ? scanner.next() : "";
            final List<Password> passwords = new CSVImporter().importFromString(data);
            importPasswords(passwords);
        } catch (IOException e) {
            displayToastInUIThread(importErrorMessage, Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }

    private void onExportFileCreated(@Nullable Uri fileURI) {
        final Activity activity = getActivity();
        final String exportErrorMessage = "Error exporting file";
        final String exportCancelledMessage = "Export cancelled";

        if (fileURI == null) {
            displayToastInUIThread(exportCancelledMessage, Toast.LENGTH_SHORT);
            return;
        }

        if (activity == null) {
            displayToastInUIThread(exportErrorMessage, Toast.LENGTH_LONG);
            return;
        }

        exportSubscription = getAllPasswords().map(csvExporter::exportToString).subscribe((result) -> {
            try (OutputStream os = activity.getContentResolver().openOutputStream(fileURI)) {
                if (os == null) {
                    throw new IOException("Failed to initialize Output stream");
                }
                os.write(result.getBytes());
                os.close();
                displayToastInUIThread("Exported successfully", Toast.LENGTH_SHORT);
            }
            catch(IOException e) {
                displayToastInUIThread(exportErrorMessage, Toast.LENGTH_LONG);
                e.printStackTrace();
            }
        });
    }
}
