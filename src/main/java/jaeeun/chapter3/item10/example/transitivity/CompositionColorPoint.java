package jaeeun.chapter3.item10.example.transitivity;

import java.util.Objects;

public class CompositionColorPoint {
    private final Point point;
    private final Color color;

    public CompositionColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    /**
     * 이 ColorPoint의 Point 뷰를 반환한다.
     */
    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompositionColorPoint))
            return false;
        CompositionColorPoint cp = (CompositionColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}
