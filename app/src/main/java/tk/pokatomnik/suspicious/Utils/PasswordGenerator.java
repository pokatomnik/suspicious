package tk.pokatomnik.suspicious.Utils;

import androidx.annotation.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;

public class PasswordGenerator implements Iterable<String> {
    private static final String ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz";

    private static final String ALPHABET_UPPER = ALPHABET_LOWER.toUpperCase();

    private static final String SYMBOLS = "!@#$%^&*()_+|{}~";

    private static final String DIGITS = "0123456789";

    private boolean useDigits;

    private boolean useSymbols;

    private int passwordLength;

    public PasswordGenerator(boolean initialUseDigits, boolean initialUseSymbols, int initialPasswordLength) {
        useDigits = initialUseDigits;
        useSymbols = initialUseSymbols;
        passwordLength = initialPasswordLength;
    }

    public boolean isUseDigits() {
        return useDigits;
    }

    public void setUseDigits(boolean useDigits) {
        this.useDigits = useDigits;
    }

    public boolean isUseSymbols() {
        return useSymbols;
    }

    public void setUseSymbols(boolean useSymbols) {
        this.useSymbols = useSymbols;
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    private String getSource() {
        final StringBuilder source = new StringBuilder(ALPHABET_LOWER);
        source.append(ALPHABET_UPPER);
        if (useSymbols) {
            source.append(SYMBOLS);
        }
        if (useDigits) {
            source.append(DIGITS);
        }
        return source.toString();
    }

    private String generate() {
        final String source = getSource();
        final Random random = new Random();
        final StringBuilder buf = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; ++i) {
            final OptionalInt useByIndexOptional = random.ints(0, source.length()).findFirst();
            if (!useByIndexOptional.isPresent()) {
                throw new RuntimeException("Invalid password length");
            }
            final int useByIndex = useByIndexOptional.getAsInt();
            buf.append(source.charAt(useByIndex));
        }
        return buf.toString();
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public String next() {
                return generate();
            }
        };
    }
}
