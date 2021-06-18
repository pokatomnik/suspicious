package tk.pokatomnik.suspicious;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class GenericRecycleViewAdapter<T>
        extends RecyclerView.Adapter<GenericRecycleViewAdapter.ViewHolder<T>> {

    public static class ViewHolder<T> extends RecyclerView.ViewHolder {
        private final LinearLayout linearLayout;

        private final BiConsumer<T, LinearLayout> fillLayout;

        private T item;

        public ViewHolder(
                LinearLayout layout,
                Consumer<T> onClick,
                BiConsumer<T, LinearLayout> layoutFiller
        ) {
            super(layout);
            linearLayout = layout;
            fillLayout = layoutFiller;
            linearLayout.setOnClickListener((view) -> {
                onClick.accept(item);
            });
        }

        public void setPassword(T newItem) {
            item = newItem;
            fillLayout.accept(item, linearLayout);
        }
    }

    private final List<T> data;

    private final Consumer<T> onClick;

    public GenericRecycleViewAdapter(
            List<T> passwordsData,
            Consumer<T> onPasswordClick
    ) {
        data = passwordsData;
        onClick = onPasswordClick;
    }

    protected abstract void fillLayout(T item, LinearLayout linearLayout);

    @NotNull
    @Override
    public GenericRecycleViewAdapter.ViewHolder<T> onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.password_item, parent, false);
        return new GenericRecycleViewAdapter.ViewHolder<T>(linearLayout, onClick, this::fillLayout);
    }

    @Override
    public void onBindViewHolder(
            @NotNull GenericRecycleViewAdapter.ViewHolder<T> viewHolder,
            int position
    ) {
        final T item = data.get(position);
        viewHolder.setPassword(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
