package tk.pokatomnik.suspicious.CustomFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import tk.pokatomnik.suspicious.ScannerActivity;

public abstract class CaptureFragment<T> extends Fragment {
    protected abstract T map(String input) throws Exception;

    private Observable<T> mapToObservable(String input) {
        try {
            return Observable.just(this.map(input));
        } catch (Exception e) {
            return Observable.never();
        }
    }

    protected final PublishSubject<String> codeCaptureSubject = PublishSubject.create();

    private final Disposable codeCaptureSubscription = codeCaptureSubject
            .switchMap(this::mapToObservable)
            .subscribe(this::onCapture);

    protected abstract void onCapture(T result);

    protected final ActivityResultLauncher<Intent> scanCode = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (result) -> {
                final int resultCode = result.getResultCode();
                if (resultCode != Activity.RESULT_OK) {
                    onCaptureCancelled();
                    return;
                }
                final Intent resultIntent = result.getData();

                if (resultIntent == null) {
                    Toast.makeText(getContext(), "Capture error", Toast.LENGTH_LONG).show();
                    return;
                }
                final String code = resultIntent.getStringExtra(ScannerActivity.INTENT_KEY);
                this.codeCaptureSubject.onNext(code);
            }
    );

    private final ActivityResultLauncher<String> singlePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            (granted) -> {
                if (granted) {
                    final Intent intent = new Intent(getContext(), ScannerActivity.class);
                    scanCode.launch(intent);
                } else if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    onHasNoPermissions();
                } else {
                    onHasNoPermissions();
                }
            }
    );

    public void capture() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            this.onHasNoPermissions();
        } else {
            singlePermission.launch(Manifest.permission.CAMERA);
        }
    }

    protected void onCaptureCancelled() {
        Toast.makeText(getContext(), "Capture cancelled", Toast.LENGTH_LONG).show();
    }

    protected void onHasNoPermissions() {
        Toast.makeText(getContext(), "Allow the app to scan QR code using camera", Toast.LENGTH_LONG).show();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        codeCaptureSubscription.dispose();
    }
}
