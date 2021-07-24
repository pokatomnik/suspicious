package tk.pokatomnik.suspicious.utils.inputdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

abstract class InputDialog {
    private final Context context;

    private final String title;

    public InputDialog(String initialTitle, Context initialContext) {
        context = initialContext;
        title = initialTitle;
    }

    @Nullable
    protected abstract Throwable validate();

    protected abstract String getText();

    protected abstract View getView();

    public void ask(BiConsumer<Throwable, String> consumer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final View view = getView();
        builder.setView(view);

        builder.setPositiveButton("OK", (dialog, which) -> {
            final Throwable error = validate();
            if (error == null) {
                consumer.accept(null, getText());
            } else {
                consumer.accept(error, null);
            }
        });

        builder.show();
    }
}
