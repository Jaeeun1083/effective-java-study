package jaeeun.chapter4.item18.example;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MainRunner {
    public static void main(String[] args) {
        Set<Instant> times = new InstrumentedSet<>(new TreeSet<>());
        InstrumentedSet<Integer> set = new InstrumentedSet<>(new HashSet<>());
    }
}
