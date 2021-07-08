package tk.pokatomnik.suspicious;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

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
            Consumer<T> onLongClick,
            Consumer<T> onRemoveClick,
            BiConsumer<T, LinearLayout> layoutFiller
        ) {
            super(layout);
            fillLayout = layoutFiller;

            linearLayout = layout.findViewById(R.id.clickable_item);
            final Button removeButton = layout.findViewById(R.id.clickable_remove);
            linearLayout.setOnClickListener((view) -> {
                onClick.accept(item);
            });
            linearLayout.setOnLongClickListener(view -> {
                onLongClick.accept(item);
                return true;
            });
            removeButton.setOnClickListener((view) -> {
                onRemoveClick.accept(item);
            });
        }

        public void setItem(T newItem) {
            item = newItem;
            fillLayout.accept(item, linearLayout);
        }
    }

    private final List<T> data;

    private final Consumer<T> onClick;

    private final Consumer<T> onLongClick;

    private final Consumer<T> onRemoveClick;

    public GenericRecycleViewAdapter(
        List<T> items,
        Consumer<T> initialOnClick,
        Consumer<T> initialOnLongClick,
        Consumer<T> initialOnRemoveClick
    ) {
        data = items;
        onClick = initialOnClick;
        onLongClick = initialOnLongClick;
        onRemoveClick = initialOnRemoveClick;
    }

    protected abstract void fillLayout(T item, LinearLayout linearLayout);

    @NotNull
    @Override
    public GenericRecycleViewAdapter.ViewHolder<T> onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType
    ) {
        final LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.layout_item, parent, false);
        return new GenericRecycleViewAdapter.ViewHolder<>(linearLayout, onClick, onLongClick, onRemoveClick, this::fillLayout);
    }

    @Override
    public void onBindViewHolder(
        @NotNull GenericRecycleViewAdapter.ViewHolder<T> viewHolder,
        int position
    ) {
        final T item = data.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull ViewHolder<T> holder) {
        super.onViewRecycled(holder);
    }
}
