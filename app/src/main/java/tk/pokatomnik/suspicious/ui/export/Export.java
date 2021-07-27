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

import java.util.Optional;

import io.reactivex.rxjava3.disposables.Disposable;
import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.databinding.FragmentExportBinding;
import tk.pokatomnik.suspicious.services.export.PasswordsExportImport;

public class Export extends Fragment {
    private final static String MIME_CSV = "text/csv";

    private final static String MIME_COMMA_SEPARATED_VALUES = "text/comma-separated-values";

    private FragmentExportBinding binding;

    final ActivityResultLauncher<String> exportLauncher =
            registerForActivityResult(new ExportActivityResultContract(MIME_CSV), this::onExportFile);

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

    private void onImportFile(@Nullable Uri fileURI) {
        importSubscription = Optional.ofNullable(getActivity()).map(((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            final PasswordsExportImport passwordsExportImportService = application.getExportService();
            return passwordsExportImportService.importPasswords(fileURI, activity, this::displayToastInUIThread);
        })).orElse(Disposable.empty());
    }

    private void onExportFile(@Nullable Uri fileUri) {
        exportSubscription = Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            final PasswordsExportImport passwordsExportImportService = application.getExportService();
            return passwordsExportImportService.exportPasswords(fileUri, activity, this::displayToastInUIThread);
        }).orElse(Disposable.empty());
    }
}
