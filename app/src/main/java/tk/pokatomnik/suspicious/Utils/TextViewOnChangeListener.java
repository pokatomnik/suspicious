package tk.pokatomnik.suspicious.Utils;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class TextViewOnChangeListener implements TextWatcher {
    private final Consumer<String> consumer;

    public TextViewOnChangeListener(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        consumer.accept(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }
}
