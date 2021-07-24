package tk.pokatomnik.suspicious.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5 {
    private final String source;

    public MD5(String initialSource) {
        source = initialSource;
    }

    @Override
    public String toString() {
        return DigestUtils.md5Hex(source).toUpperCase();
    }
}
