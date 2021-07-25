package tk.pokatomnik.suspicious.services.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import tk.pokatomnik.suspicious.services.database.entities.Password;

@Dao
public interface PasswordDAO {
    @Query("SELECT * FROM password")
    Single<List<Password>> getAll();

    @Query("SELECT * FROM password WHERE uid = :uid")
    Single<Password> getByUID(int uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Password... passwords);

    @Delete
    Completable delete(Password password);

    @Update
    Completable update(Password password);
}
