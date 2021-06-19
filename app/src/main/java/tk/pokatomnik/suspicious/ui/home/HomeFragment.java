package tk.pokatomnik.suspicious.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import tk.pokatomnik.suspicious.CustomFragments.DomainCaptureFragment;
import tk.pokatomnik.suspicious.Entities.Password;
import tk.pokatomnik.suspicious.databinding.FragmentHomeBinding;

public class HomeFragment extends DomainCaptureFragment {
    private FragmentHomeBinding binding;

    private PasswordsExtractor passwordsExtractor;

    private PasswordsRecyclerViewManager passRecycleViewManager;

    private BehaviorSubject<String> searchTextSubject;

    @Nullable
    private Disposable passwordClickSubscription;

    @Nullable
    private Disposable searchTextSubscription;

    @Nullable
    private Disposable searchPasswordsSubscription;

    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        passwordsExtractor = new PasswordsExtractor(getContext(), getActivity());
        passRecycleViewManager = new PasswordsRecyclerViewManager(binding.recyclerView, getContext());

        searchTextSubject = BehaviorSubject.createDefault(binding.searchView.getQuery().toString());
        binding.searchView.setOnQueryTextListener(new SearchViewOnChangeListener(searchTextSubject::onNext));
        searchTextSubscription = searchTextSubject.distinctUntilChanged().subscribe((newText) -> {
            binding.searchView.setQuery(newText, false);
        });

        searchPasswordsSubscription = Observable
            .combineLatest(
                searchTextSubject.distinctUntilChanged(),
                passwordsExtractor.getPasswordsObservable().distinctUntilChanged(),
                this::applySearch
            )
            .debounce(1, TimeUnit.SECONDS)
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

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        passwordsExtractor.extract();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Optional.ofNullable(passwordClickSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(searchTextSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(searchPasswordsSubscription).ifPresent(Disposable::dispose);
        passRecycleViewManager.dispose();
        passwordsExtractor.dispose();
    }

    private List<Password> applySearch(String searchString, List<Password> source) {
        if (searchString.equals("")) {
            return source;
        }
        return source.stream().filter(((password) -> {
            return password.match(searchString);
        })).collect(Collectors.toList());
    }

    private void handlePasswordClick(Password password) {
    }

    @Override
    protected void onCapture(String result) {
        searchTextSubject.onNext(result);
    }
}