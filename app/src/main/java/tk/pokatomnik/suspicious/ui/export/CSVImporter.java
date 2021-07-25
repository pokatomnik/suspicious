package tk.pokatomnik.suspicious.ui.export;

import java.util.ArrayList;
import java.util.List;

import tk.pokatomnik.suspicious.services.database.entities.Password;
import tk.pokatomnik.suspicious.ui.export.CSV.PasswordCSVUtils;

public class CSVImporter {
    public List<Password> importFromString(String source) {
        final List<Password> result = new ArrayList<>(50);
        final String[] lines = source.split(PasswordCSVUtils.getRowDelimiter());

        if (lines.length <= 1) {
            return result;
        }

        for (int i = 1; i < lines.length; ++i) {
            final String line = lines[i];
            try {
                Password password = PasswordCSVUtils.getPasswordFromLine(line);
                if (password != null) {
                    result.add(password);
                }
            } catch (Throwable ignored) {}
        }

        return result;
    }
}
