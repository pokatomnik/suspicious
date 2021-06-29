package tk.pokatomnik.suspicious.Utils;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Optional;

import io.reactivex.rxjava3.functions.Consumer;

public class ToastError<T extends Throwable> implements Consumer<T> {
    @Nullable
    private final Activity activity;

    private final String text;

    public ToastError(@Nullable Activity initialActivity, String initialText) {
        activity = initialActivity;
        text = initialText;
    }

    @Override
    public void accept(Throwable throwable) {
        Optional.ofNullable(activity).ifPresent((activity) -> {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_LONG).show();
            });
        });

    }
}
