package tk.pokatomnik.suspicious.ui.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import tk.pokatomnik.suspicious.Entities.Password;

public class CSVExporter implements Exporter<Password> {
    private static final String HEADER = "\"ID\",\"DOMAIN\",\"USER NAME\",\"PASSWORD\",\"COMMENT\"";

    private String formatCell(String data) {
        final StringBuilder builder = new StringBuilder("");
        for (final String character: data.split("")) {
            if (character.equals("\"")) {
                builder.append("\"\"");
            } else {
                builder.append(character);
            }
        }
        return String.format("\"%s\"", builder.toString());
    }

    private String formatRow(Password password) {
        final List<String> row = new ArrayList<>(4);
        row.add(formatCell(String.valueOf(password.getUid())));
        row.add(formatCell(password.getDomain()));
        row.add(formatCell(password.getUserName()));
        row.add(formatCell(password.getPassword()));
        row.add(formatCell(password.getComment()));
        return String.join(",", row);
    }

    @Override
    public String export(List<Password> input) {
        final List<String> header = Collections.singletonList(HEADER);
        final List<String> body = input.stream().map(this::formatRow).collect(Collectors.toList());
        final List<String> finalList = new ArrayList<>();
        finalList.addAll(header);
        finalList.addAll(body);
        return String.join("\n", finalList);
    }
}
