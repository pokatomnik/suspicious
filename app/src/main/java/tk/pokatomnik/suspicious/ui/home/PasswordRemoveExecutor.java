package tk.pokatomnik.suspicious.ui.home;

import androidx.annotation.Nullable;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import tk.pokatomnik.suspicious.services.database.entities.Password;
import tk.pokatomnik.suspicious.SuspiciousApplication;

public class PasswordRemoveExecutor implements Disposable {
    private boolean isDisposed = false;

    @Nullable
    private Disposable passwordRemoveSubscription;

    private final Consumer<Throwable> onError;

    private final SuspiciousApplication application;

    private final LinkedBlockingQueue<Password> removeQueue = new LinkedBlockingQueue<>();

    private final ExecutorService removeExecutorService = Executors.newSingleThreadExecutor();

    public PasswordRemoveExecutor(
        SuspiciousApplication initialApplication,
        Consumer<Throwable> initialOnError
    ) {
        application = initialApplication;
        onError = initialOnError;
    }

    public void queueRemove(Password password) {
        removeQueue.offer(password);
    }

    public void initialize() {
        removeExecutorService.execute(() -> {
            try {
                while (true) {
                    final Password password = removeQueue.take();
                    removePasswordFromDB(password);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void dispose() {
        removeExecutorService.shutdownNow();
        Optional.ofNullable(passwordRemoveSubscription).ifPresent(Disposable::dispose);
        isDisposed = true;
    }

    @Override
    public boolean isDisposed() {
        return isDisposed;
    }

    private void removePasswordFromDB(Password password) {
        passwordRemoveSubscription = application
            .getPasswordDatabaseService()
            .delete(password)
            .subscribe(() -> {}, onError);
    }
}
