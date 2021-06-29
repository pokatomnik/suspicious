package tk.pokatomnik.suspicious.Storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tk.pokatomnik.suspicious.DAO.PasswordDAO;
import tk.pokatomnik.suspicious.Entities.Password;

@Database(entities = {Password.class}, version = 1, exportSchema = false)
public abstract class PasswordDatabase extends RoomDatabase {
    public abstract PasswordDAO passwordDAO();
}
