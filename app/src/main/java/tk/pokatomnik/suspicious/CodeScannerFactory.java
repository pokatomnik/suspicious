package tk.pokatomnik.suspicious;

import android.content.Context;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.Collections;

public class CodeScannerFactory {
    public static CodeScanner make(@NonNull final Context context, @NonNull final CodeScannerView view) {
        final CodeScanner codeScanner = new CodeScanner(context, view);

        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(new ArrayList<>(Collections.singleton(BarcodeFormat.QR_CODE)));
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.SINGLE);
        codeScanner.setFlashEnabled(false);

        return codeScanner;
    }
}
