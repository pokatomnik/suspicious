package tk.pokatomnik.suspicious.utils.inputdialog;

import android.content.Context;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.Nullable;

import tk.pokatomnik.suspicious.utils.TextViewOnChangeListener;

public class PasswordInputDialog extends InputDialog {
    private final EditText editText;

    public PasswordInputDialog(String initialTitle, Context initialContext) {
        super(initialTitle, initialContext);
        final EditText input = new EditText(initialContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText = input;
    }

    @Nullable
    @Override
    protected Throwable validate() {
        return null;
    }

    @Override
    protected String getText() {
        return editText.getText().toString();
    }

    @Override
    protected EditText getView() {
        return editText;
    }
}
