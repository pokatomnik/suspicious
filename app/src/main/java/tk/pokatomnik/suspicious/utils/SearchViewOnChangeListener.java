package tk.pokatomnik.suspicious.utils;

import android.widget.SearchView;

import java.util.function.Consumer;

public class SearchViewOnChangeListener implements SearchView.OnQueryTextListener {
    private final Consumer<String> onChange;

    public SearchViewOnChangeListener(Consumer<String> onChangeListener) {
        onChange = onChangeListener;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        onChange.accept(newText);
        return true;
    }
}
