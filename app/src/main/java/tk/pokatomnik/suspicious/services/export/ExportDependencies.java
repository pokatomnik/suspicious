package tk.pokatomnik.suspicious.services.export;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import tk.pokatomnik.suspicious.services.database.entities.Password;

public class ExportDependencies {
    private final Supplier<Single<List<Password>>> getAll;

    private final Function<Password[], Completable> insert;

    public ExportDependencies(
        Supplier<Single<List<Password>>> initialGetAll,
        Function<Password[], Completable> initialInsert
    ) {
        getAll = initialGetAll;
        insert = initialInsert;
    }

    public Single<List<Password>> getAll() {
        return getAll.get();
    }

    public Completable insert(Password... passwords) {
        return insert.apply(passwords);
    }
}
