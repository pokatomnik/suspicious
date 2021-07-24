package tk.pokatomnik.suspicious.utils.inputdialog;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import tk.pokatomnik.suspicious.utils.TextViewOnChangeListener;

public class NewPasswordInputDialog extends InputDialog {
    private final LinearLayout layout;

    private final EditText password;

    private final EditText passwordConfirmation;

    public NewPasswordInputDialog(String initialTitle, Context initialContext) {
        super(initialTitle, initialContext);
        layout = new LinearLayout(initialContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        password = new EditText(initialContext);
        passwordConfirmation = new EditText(initialContext);

        password.setHint("Password");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.addTextChangedListener(new TextViewOnChangeListener((view) -> {
            password.setError(null);
            passwordConfirmation.setError(null);
        }));

        passwordConfirmation.setHint("Confirm Password");
        passwordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordConfirmation.addTextChangedListener(new TextViewOnChangeListener((view) -> {
            password.setError(null);
            passwordConfirmation.setError(null);
        }));

        layout.addView(password);
        layout.addView(passwordConfirmation);
    }

    @Override
    protected Throwable validate() {
        boolean emptyPassword = false;
        boolean emptyPasswordConfirmation = false;
        boolean passwordsMismatch = false;
        final String passwordText = password.getText().toString();
        final String passwordConfirmationText = passwordConfirmation.getText().toString();

        if (passwordText.length() == 0) {
            emptyPassword = true;
        }
        if (passwordConfirmationText.length() == 0) {
            emptyPasswordConfirmation = true;
        }
        if (!emptyPassword && !emptyPasswordConfirmation && !passwordText.equals(passwordConfirmationText)) {
            passwordsMismatch = true;
        }

        if (emptyPassword) {
            return new Throwable("Empty Password");
        } else if (passwordsMismatch) {
            return new Throwable("Password Confirmation is different");
        } else {
            return null;
        }
    }

    @Override
    protected String getText() {
        return password.getText().toString();
    }

    @Override
    protected View getView() {
        return layout;
    }
}
