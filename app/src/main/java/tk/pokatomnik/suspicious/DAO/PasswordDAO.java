package tk.pokatomnik.suspicious.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import tk.pokatomnik.suspicious.Entities.Password;

@Dao
public interface PasswordDAO {
    @Query("SELECT * FROM password")
    Single<List<Password>> getAll();

    @Query("Select * from password WHERE uid IN (:userIds)")
    Single<List<Password>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM password WHERE domain LIKE :domainParam LIMIT 1")
    Single<Password> findByDomain(String domainParam);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Password... passwords);

    @Delete
    Completable delete(Password password);
}
