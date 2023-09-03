package jaeeun.chapter3.item10.example.symmetry;

import java.util.Objects;

public class CaseInsensitiveString {
    private final String s;

    public CaseInsensitiveString(String s) {
        this.s = Objects.requireNonNull(s);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString) return s.equalsIgnoreCase(((CaseInsensitiveString) o).s);
        if (o instanceof String) return s.equalsIgnoreCase(((String) o));
        return false;
    }

}
