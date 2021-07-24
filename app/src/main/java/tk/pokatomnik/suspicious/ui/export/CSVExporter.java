package tk.pokatomnik.suspicious.ui.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import tk.pokatomnik.suspicious.entities.Password;
import tk.pokatomnik.suspicious.ui.export.CSV.PasswordCSVUtils;

public class CSVExporter {
    public String exportToString(List<Password> input) {
        final List<String> header = Collections.singletonList(PasswordCSVUtils.getHeader());
        final List<String> body = input.stream().map(PasswordCSVUtils::formatRow).collect(Collectors.toList());
        final List<String> finalList = new ArrayList<>();
        finalList.addAll(header);
        finalList.addAll(body);
        return String.join(PasswordCSVUtils.getRowDelimiter(), finalList);
    }
}
