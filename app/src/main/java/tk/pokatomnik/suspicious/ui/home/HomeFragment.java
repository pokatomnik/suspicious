package tk.pokatomnik.suspicious.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import tk.pokatomnik.suspicious.CustomFragments.DomainCaptureFragment;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.R;
import tk.pokatomnik.suspicious.SuspiciousApplication;
import tk.pokatomnik.suspicious.Utils.Confirmation;
import tk.pokatomnik.suspicious.Utils.Matchers.FuzzyMatcher;
import tk.pokatomnik.suspicious.Utils.Matchers.IncludeMatcher;
import tk.pokatomnik.suspicious.Utils.Matchers.Matcher;
import tk.pokatomnik.suspicious.Utils.ObservablePrimitiveValueConnector;
import tk.pokatomnik.suspicious.Utils.SearchViewOnChangeListener;
import tk.pokatomnik.suspicious.databinding.FragmentHomeBinding;
import tk.pokatomnik.suspicious.ui.editpassword.EditPasswordFragment;

public class HomeFragment extends DomainCaptureFragment {
    private final LinkedBlockingQueue<Password> removeQueue = new LinkedBlockingQueue<>();

    private final ExecutorService removeExecutorService = Executors.newSingleThreadExecutor();

    private FragmentHomeBinding binding;

    private PasswordsExtractor passwordsExtractor;

    private PasswordsRecyclerViewManager passRecycleViewManager;

    private ObservablePrimitiveValueConnector<String> searchTextObservable;

    @Nullable
    private Disposable passwordClickSubscription;

    @Nullable
    private Disposable passwordRemoveClickSubscription;

    @Nullable
    private Disposable searchPasswordsSubscription;

    @Nullable
    private Disposable passwordRemoveSubscription;

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        passwordsExtractor = new PasswordsExtractor(getActivity());
        passRecycleViewManager = new PasswordsRecyclerViewManager(binding.recyclerView, getContext());

        searchTextObservable = new ObservablePrimitiveValueConnector<>(
            () -> binding.searchView.getQuery().toString(),
            (newValue) -> binding.searchView.setQuery(newValue, false),
            (onNext) -> binding.searchView.setOnQueryTextListener(new SearchViewOnChangeListener(onNext))
        );

        searchPasswordsSubscription = Observable
            .combineLatest(
                searchTextObservable.observe().distinctUntilChanged(),
                passwordsExtractor.getPasswordsObservable().distinctUntilChanged(),
                this::applyFuzzySearch
            )
            .subscribe((results) -> {
                Optional.ofNullable(getActivity()).ifPresent((activity) -> {
                    activity.runOnUiThread(() -> {
                        passRecycleViewManager.updatePasswords(results);
                    });
                });
            });

        binding.floatingActionButton.setOnClickListener((View view) -> capture());

        passwordClickSubscription = passRecycleViewManager
            .getClickSubject()
            .subscribe(this::handlePasswordClick);

        passwordRemoveClickSubscription = passRecycleViewManager
            .getClickRemoveSubject()
            .subscribe(this::handlePasswordRemoveClick);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        removeExecutorService.execute(() -> {
            try {
                while (true) {
                    final Password password = removeQueue.take();
                    removePasswordFromDB(password, (removeError) -> {
                        passwordsExtractor.extract();
                        Optional.ofNullable(getActivity()).ifPresent((activity) -> {
                            activity.runOnUiThread(() -> {
                                Toast.makeText(
                                    getContext(),
                                    "Failed to remove password",
                                    Toast.LENGTH_LONG
                                ).show();
                            });
                        });
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        passwordsExtractor.extract();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Optional.ofNullable(passwordClickSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(passwordRemoveClickSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(searchTextObservable).ifPresent(Disposable::dispose);
        Optional.ofNullable(searchPasswordsSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(passwordRemoveSubscription).ifPresent(Disposable::dispose);
        passRecycleViewManager.dispose();
        passwordsExtractor.dispose();
    }

    private List<Password> applySearch(String searchString, List<Password> source) {
        final Matcher<Password> includeMatcher = new IncludeMatcher<>((query, password) -> {
            final String matchLower = query.toLowerCase();
            final boolean matchDomain = password.getDomain().toLowerCase().contains(matchLower);
            final boolean matchUserName = password.getUserName().toLowerCase().contains(matchLower);
            final boolean matchComment = password.getComment().toLowerCase().contains(matchLower);
            return matchDomain || matchUserName || matchComment ? 1 : 0;
        });
        return includeMatcher.getResults(searchString, source);
    }

    private List<Password> applyFuzzySearch(String searchString, List<Password> source) {
        final Matcher<Password> fuzzyMatcher = new FuzzyMatcher<>((query, password, fuzzyScore) -> {
            final String queryLower = query.toLowerCase();
            final int domainScore = fuzzyScore.fuzzyScore(password.getDomain().toLowerCase(), queryLower);
            final int userNameScore = fuzzyScore.fuzzyScore(password.getUserName().toLowerCase(), queryLower);
            final int commentScore = fuzzyScore.fuzzyScore(password.getComment().toLowerCase(), queryLower);

            return Math.max(Math.max(domainScore, userNameScore), commentScore);
        });

        return fuzzyMatcher.getResults(searchString, source);
    }

    private void handlePasswordClick(Password password) {
        Optional.ofNullable(getActivity()).ifPresent((activity) -> {
            final Bundle bundle = new Bundle();
            bundle.putInt(EditPasswordFragment.PASSWORD_ID_KEY, password.getUid());
            Navigation
                .findNavController(getActivity(), R.id.nav_host_fragment_content_main)
                .navigate(R.id.nav_edit_password, bundle);
        });
    }

    private void handlePasswordRemoveClick(Password password) {
        new Confirmation(getContext())
            .setTitle("Confirm removing")
            .setDescription(String.format("Are you sure to remove password for %s", password.getDomain()))
            .onYes(() -> {
                removePasswordFromList(password);
                removeQueue.offer(password);
            })
            .confirm();
    }

    private void removePasswordFromList(Password password) {
        final List<Password> passwordsWithRemovedPassword =
            Optional
                .ofNullable(passwordsExtractor.getPasswordsObservable().getValue())
                .orElse(new ArrayList<>())
                .stream()
                .filter((currentPassword) -> currentPassword.getUid() != password.getUid())
                .collect(Collectors.toList());
        passwordsExtractor.getPasswordsObservable().onNext(passwordsWithRemovedPassword);
    }

    private void removePasswordFromDB(Password password, Consumer<Throwable> onError) {
        passwordRemoveSubscription = Optional.ofNullable(getActivity()).map((activity) -> {
            final SuspiciousApplication application = (SuspiciousApplication) activity.getApplication();
            return application
                .getPasswordDatabase()
                .passwordDAO()
                .delete(password)
                .subscribe(() -> {}, onError);
        }).orElse(Disposable.empty());
    }

    @Override
    protected void onCapture(String result) {
        searchTextObservable.next(result);
    }
}