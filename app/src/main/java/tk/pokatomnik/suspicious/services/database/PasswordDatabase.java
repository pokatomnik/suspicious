package tk.pokatomnik.suspicious.services.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tk.pokatomnik.suspicious.services.database.dao.PasswordDAO;
import tk.pokatomnik.suspicious.services.database.entities.Password;

@Database(entities = {Password.class}, version = 1, exportSchema = false)
public abstract class PasswordDatabase extends RoomDatabase {
    public abstract PasswordDAO passwordDAO();
}
