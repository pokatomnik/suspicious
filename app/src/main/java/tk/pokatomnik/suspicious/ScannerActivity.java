package tk.pokatomnik.suspicious;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.zxing.Result;

import java.util.Optional;

import tk.pokatomnik.suspicious.databinding.ActivityScannerBinding;

public class ScannerActivity extends AppCompatActivity {
    public final static String INTENT_KEY = "CODE_SCANNER_RESULT";

    @Nullable
    private CodeScanner codeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityScannerBinding binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        codeScanner = CodeScannerFactory.make(this, binding.scannerView);
        codeScanner.setDecodeCallback(this::onDecoded);
        binding.scannerView.setOnClickListener(this::handleClick);
    }

    private void onDecoded(@NonNull final Result result) {
        runOnUiThread(() -> {
            final Intent resultIntent = new Intent();
            resultIntent.putExtra(INTENT_KEY, result.getText());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void handleClick(View view) {
        Optional.ofNullable(codeScanner).ifPresent(CodeScanner::startPreview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Optional.ofNullable(codeScanner).ifPresent(CodeScanner::startPreview);
    }

    @Override
    protected void onPause() {
        Optional.ofNullable(codeScanner).ifPresent(CodeScanner::releaseResources);
        super.onPause();
    }
}