package jaeeun.chapter3.item10.example.transitivity;

public class IgnoreTransitivityColorPoint extends Point {
    private final Color color;

    public IgnoreTransitivityColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point))
            return false;

        // o가 일반 Point면 색상을 무시하고 비교한다.
        if (!(o instanceof IgnoreTransitivityColorPoint))
            return o.equals(this);

        // o가 ColorPoint면 색상까지 비교한다.
        return super.equals(o) && ((IgnoreTransitivityColorPoint) o).color == color;
    }
}
