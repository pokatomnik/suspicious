package tk.pokatomnik.suspicious.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public final class Password {
    public Password(String domain, String userName, String password, String comment) {
        this.domain = domain;
        this.userName = userName;
        this.password = password;
        this.comment = comment;
    }

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "domain")
    public String domain;

    @ColumnInfo(name = "username")
    public String userName;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "comment")
    public String comment;
}
