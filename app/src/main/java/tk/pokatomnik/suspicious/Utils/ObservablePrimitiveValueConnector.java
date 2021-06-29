package tk.pokatomnik.suspicious.Utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ObservablePrimitiveValueConnector<T> implements Disposable {
    private final BehaviorSubject<T> behaviorSubject;

    private final Disposable subscription;

    private boolean isDisposed = false;

    private final Supplier<T> getter;

    public ObservablePrimitiveValueConnector(
        Supplier<T> getValue,
        Consumer<T> setValue,
        Consumer<Consumer<T>> subscribeToEvent
    ) {
        getter = getValue;
        behaviorSubject = BehaviorSubject.createDefault(getValue.get());
        subscription = behaviorSubject
            .distinctUntilChanged((unused, newValue) -> {
                /*
                 * The first parameter (unused) is a param from the Behaviour subject,
                 * and we must not use it to prevent looped updates. Instead we're using
                 * the getter to get the newest value
                 */
                final T oldValue = getter.get();
                return compareEquality(oldValue, newValue);
            })
            .subscribe(setValue::accept);
        subscribeToEvent.accept(this::next);
    }

    /**
     * This method is about checking if a previous value is the same as a new one
     * @param oldValue old value from the UI component
     * @param newValue new value intended to be set
     * @return true if objects are the same, or false otherwise
     */
    protected boolean compareEquality(T oldValue, T newValue) {
        return Objects.equals(oldValue, newValue);
    }

    public void next(T newValue) {
        behaviorSubject.onNext(newValue);
    }

    public T getValue() {
        return behaviorSubject.getValue();
    }

    public Observable<T> observe() {
        return behaviorSubject.hide();
    }

    @Override
    public void dispose() {
        isDisposed = true;
        subscription.dispose();
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }
}
