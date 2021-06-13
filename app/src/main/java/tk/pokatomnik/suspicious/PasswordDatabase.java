package tk.pokatomnik.suspicious;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import tk.pokatomnik.suspicious.DAO.PasswordDAO;
import tk.pokatomnik.suspicious.Entities.Password;

@Database(entities = {Password.class}, version = 1)
public abstract class PasswordDatabase extends RoomDatabase {
    public abstract PasswordDAO passwordDAO();
}
