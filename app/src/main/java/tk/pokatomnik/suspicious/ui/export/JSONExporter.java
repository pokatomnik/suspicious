package tk.pokatomnik.suspicious.ui.export;

import com.google.gson.Gson;

import java.util.List;

import tk.pokatomnik.suspicious.Entities.Password;

public class JSONExporter implements Exporter<Password> {
    @Override
    public String export(List<Password> input) {
        return new Gson().toJson(input);
    }
}
