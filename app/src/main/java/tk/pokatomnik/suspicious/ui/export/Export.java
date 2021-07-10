package tk.pokatomnik.suspicious.ui.export;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.R;
import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.databinding.FragmentExportBinding;

public class Export extends Fragment {
    private final static String MIME_CSV = "text/csv";

    private final static String MIME_JSON = "application/json";

    private final static String MIME_TEXT = "text/plain";

    private FragmentExportBinding binding;

    @Nullable
    private Disposable exportSubscription;

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

    private String getMime(String desiredMime) {
        if (binding.shareAsText.isChecked()) {
            return MIME_TEXT;
        }
        return desiredMime;
    }

    @Override
    public void onViewCreated(
        @NonNull View view,
        @Nullable Bundle savedInstanceState
    ) {
        binding.exportCSVSimple.setOnClickListener(this::handleExportCSVSimple);
        binding.exportJSONSimple.setOnClickListener(this::handleExportJSONSimple);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Optional.ofNullable(exportSubscription).ifPresent(Disposable::dispose);
    }

    private Single<List<Password>> getAllPasswords() {
        return Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            return application.getPasswordDatabase().passwordDAO().getAll().subscribeOn(Schedulers.single());
        }).orElse(Single.never());
    }

    private void handleExportCSVSimple(View view) {
        final Exporter<Password> csvExporter = new CSVExporter();
        exportSubscription = getAllPasswords().map(csvExporter::export).subscribe((result) -> {
            handleResult(result, MIME_CSV);
        });
    }

    private void handleExportJSONSimple(View view) {
        final Exporter<Password> jsonExporter = new JSONExporter();
        exportSubscription = getAllPasswords().map(jsonExporter::export).subscribe((result) -> {
            handleResult(result, MIME_JSON);
        });
    }

    private void handleResult(String result, String mime) {
        final Intent shareIntent = new Intent();
        final String actualMime = getMime(mime);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, result);
        shareIntent.setType(actualMime);
        final Intent chooserIntent = Intent.createChooser(
            shareIntent,
            getResources().getText(R.string.export_intent_caption)
        );
        startActivity(chooserIntent);
    }
}
