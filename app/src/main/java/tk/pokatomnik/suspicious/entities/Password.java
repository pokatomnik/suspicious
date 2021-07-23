package tk.pokatomnik.suspicious.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import tk.pokatomnik.suspicious.utils.encryption.BlowfishEncryption;
import tk.pokatomnik.suspicious.utils.encryption.TextEncryption;

@Entity
public final class Password {
    public Password(String domain, String userName, String password, String comment) {
        this.domain = domain;
        this.userName = userName;
        this.password = password;
        this.comment = comment;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "domain")
    private String domain;

    @ColumnInfo(name = "username")
    private String userName;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "comment")
    private String comment;

    public int getUid() {
        return uid;
    }

    public String getDomain() {
        return domain;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getComment() {
        return comment;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
