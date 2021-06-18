package tk.pokatomnik.suspicious.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

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
    private Disposable allPasswordsSubscription;

    @Nullable
    private Disposable passwordClickSubscription;

    @Nullable
    private Disposable searchTextSubscription;

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

        binding.floatingActionButton.setOnClickListener((View view) -> capture());

        passwordClickSubscription = passRecycleViewManager
                .getClickSubject()
                .subscribe(this::handlePasswordClick);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.refreshPasswords();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Optional.ofNullable(allPasswordsSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(passwordClickSubscription).ifPresent(Disposable::dispose);
        Optional.ofNullable(searchTextSubscription).ifPresent(Disposable::dispose);
    }

    private void refreshPasswords() {
        allPasswordsSubscription = passwordsExtractor.extract(passRecycleViewManager::updatePasswords);
    }

    private void handlePasswordClick(Password password) {}

    @Override
    protected void onCapture(String result) {
        searchTextSubject.onNext(result);
    }
}