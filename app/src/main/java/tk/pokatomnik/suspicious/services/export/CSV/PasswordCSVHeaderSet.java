package tk.pokatomnik.suspicious.services.export.CSV;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import tk.pokatomnik.suspicious.services.database.entities.Password;

class PasswordCSVHeaderSet {
    private static final String DOMAIN = "DOMAIN";

    private static final String USER_NAME = "USER NAME";

    private static final String PASSWORD = "PASSWORD";

    private static final String COMMENT = "COMMENT";

    private static final Map<String, Function<Password, String>> TITLES =
        new LinkedHashMap<String, Function<Password, String>>() {{
            put(DOMAIN, Password::getDomain);
            put(USER_NAME, Password::getUserName);
            put(PASSWORD, Password::getPassword);
            put(COMMENT, Password::getComment);
        }};

    private static final List<String> keys = new ArrayList<>(TITLES.keySet());

    public static final String HEADER = PasswordCSVHeaderSet.TITLES.keySet().stream().map((it) -> {
        return String.format("\"%s\"", it);
    }).collect(Collectors.joining(","));

    public static Map<String, Function<Password, String>> getTitles() {
        return TITLES;
    }

    public static int getLength() {
        return TITLES.size();
    }

    public static int getDomainIndex() {
        return keys.indexOf(DOMAIN);
    }

    public static int getUsernameIndex() {
        return keys.indexOf(USER_NAME);
    }

    public static int getPasswordIndex() {
        return keys.indexOf(PASSWORD);
    }

    public static int getCommentIndex() {
        return keys.indexOf(COMMENT);
    }
}
