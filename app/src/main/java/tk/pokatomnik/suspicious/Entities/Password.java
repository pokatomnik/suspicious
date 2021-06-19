package tk.pokatomnik.suspicious.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import tk.pokatomnik.suspicious.Utils.Match;

@Entity
public final class Password implements Match {
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

    @Override
    public boolean match(String query) {
        final String matchLower = query.toLowerCase();
        final boolean matchDomain = domain.toLowerCase().contains(matchLower);
        final boolean matchUserName = userName.toLowerCase().contains(matchLower);
        final boolean matchComment = userName.toLowerCase().contains(matchLower);
        return matchDomain || matchUserName || matchComment;
    }
}
