package jaeeun.chapter3.item14.example;

import java.util.Comparator;

import static java.util.Comparator.comparingInt;

public class PhoneNumber implements Comparable<PhoneNumber> {
    private short areaCode, prefix, lineNum;
    private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);

    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
}
