package tk.pokatomnik.suspicious.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import java.util.function.Consumer;

public class InputDialog {
    private static int INPUT_TYPE = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

    private final Context context;

    private final String title;

    public InputDialog(String initialTitle, Context initialContext) {
        context = initialContext;
        title = initialTitle;
    }

    public void ask(Consumer<String> consumer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        final EditText input = new EditText(context);
        input.setInputType(INPUT_TYPE);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            final String text = input.getText().toString();
            consumer.accept(text);
        });

        builder.show();
    }
}
