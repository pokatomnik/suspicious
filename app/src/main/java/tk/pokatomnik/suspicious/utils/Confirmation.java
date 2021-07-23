package tk.pokatomnik.suspicious.utils;

import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import tk.pokatomnik.suspicious.R;

public final class Confirmation {
    private final Context context;

    private String title;

    private String description;

    private EmptyCallback yesCallback = () -> {};

    private EmptyCallback noCallback = () -> {};

    public Confirmation(Context initialContext) {
        context = initialContext;
    }

    public void confirm() {
        new AlertDialog
            .Builder(context)
            .setTitle(title)
            .setMessage(description)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.answer_yes, (dialog, which) -> {
                yesCallback.invoke();
            })
            .setNegativeButton(R.string.answer_no, ((dialog, which) -> {
                noCallback.invoke();
            }))
            .show();
    }

    public Confirmation setTitle(@NonNull String title) {
        this.title = title;
        return this;
    }

    public Confirmation setDescription(@NonNull String description) {
        this.description = description;
        return this;
    }

    public Confirmation onYes(@NonNull EmptyCallback callback) {
        yesCallback = callback;
        return this;
    }

    public Confirmation onNo(@NonNull EmptyCallback callback) {
        noCallback = callback;
        return this;
    }
}
