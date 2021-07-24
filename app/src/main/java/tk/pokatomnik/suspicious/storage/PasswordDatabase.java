package tk.pokatomnik.suspicious.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tk.pokatomnik.suspicious.dao.PasswordDAO;
import tk.pokatomnik.suspicious.entities.Password;

@Database(entities = {Password.class}, version = 1, exportSchema = false)
public abstract class PasswordDatabase extends RoomDatabase {
    public abstract PasswordDAO passwordDAO();
}
