package tk.pokatomnik.suspicious.ui.export.CSV;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tk.pokatomnik.suspicious.services.database.entities.Password;

public class PasswordCSVUtils {
    private static final String ROW_DELIMITER = "\n";

    private static final String CELL_DELIMITER = ",";

    private static String wrapCellContents(String input) {
        if (input == null) {
            throw new NullPointerException("Input string is null");
        }
        return String.format("\"%s\"", input);
    }

    public static String formatRow(Password password) {
        final List<String> row = new ArrayList<>(PasswordCSVHeaderSet.getLength());
        PasswordCSVHeaderSet.getTitles().values().forEach((mapper) -> {
            row.add(formatCell(mapper.apply(password)));
        });
        return String.join(CELL_DELIMITER, row);
    }

    @Nullable
    public static Password getPasswordFromLine(String line) {
        final String[] parts = line.split(PasswordCSVUtils.CELL_DELIMITER);
        if (!isCSVPasswordRow(parts)) {
            return null;
        }

        final String domain = cleanupValue(parts[PasswordCSVHeaderSet.getDomainIndex()]);
        final String username = cleanupValue(parts[PasswordCSVHeaderSet.getUsernameIndex()]);
        final String password = cleanupValue(parts[PasswordCSVHeaderSet.getPasswordIndex()]);
        final String comment = cleanupValue(parts[PasswordCSVHeaderSet.getCommentIndex()]);

        return new Password(domain, username, password, comment);
    }

    public static String getRowDelimiter() {
        return ROW_DELIMITER;
    }

    public static String getHeader() {
        return PasswordCSVHeaderSet.HEADER;
    }

    private static String formatCell(String data) {
        final StringBuilder builder = new StringBuilder();
        for (final String character: data.split("")) {
            if (character.equals("\"")) {
                builder.append("\"\"");
            } else {
                builder.append(character);
            }
        }
        return wrapCellContents(builder.toString());
    }

    private static String cleanupValue(String valueRaw) {
        if (valueRaw.length() < 2) {
            return "";
        }
        if (valueRaw.equals("\"\"")) {
            return valueRaw;
        }
        return valueRaw.substring(1, valueRaw.length() - 1).replaceAll("\"\"", "\"");
    }

    private static boolean isCSVPasswordRow(String[] rows) {
        return rows.length == PasswordCSVHeaderSet.getLength();
    }
}
