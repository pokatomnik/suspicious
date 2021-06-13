package tk.pokatomnik.suspicious.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tk.pokatomnik.suspicious.Entities.Password;

@Dao
public interface PasswordDAO {
    @Query("SELECT * FROM password")
    List<Password> getAll();

    @Query("Select * from password WHERE uid IN (:userIds)")
    List<Password> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM password WHERE domain LIKE :domainParam LIMIT 1")
    Password findByDomain(String domainParam);

    @Insert
    void insert(Password... passwords);

    @Delete
    void delete(Password password);
}
