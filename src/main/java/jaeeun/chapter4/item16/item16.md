## [item 16] public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라.

- public 클래스의 필드 접근성이 public 이라면 데이터 필드에 직접 접근할 수 있으니 캡슐화의 이점을 제공하지 못한다.

```java
class Point {
    public double x;
	public double y;
}
```
### public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하자

```java
class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }

    public void setX(double x) { this.x = x; }

    public double getY() { return y; }

    public void setY(double y) { this.y = y; }
}
```
- 패키지 바깥에서 접근할 수 있는 클래스라면 접근자를 제공함으로써 클래스 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 갖게 하는 것이 좋다

### package-private 클래스 혹은 private 중첩 클래스의 데이터 필드의 노출
- package-private 클래스 혹은 private 중첩 클래스에서 데이터 필드를 노출하는 것은 큰 문제가 되지 않는다.
  - 클래스가 표현하려는 추상 개념만 올바르게 표현해주면 된다.
- package-private 클래스에서 public으로 필드를 노출하는 경우 동일 패키지 내에서 접근할 때 더 간결하다는 장점이 있다 또한 만약 변경되는 것이 바람직하다면 패키지 외부의 소스 수정 없이 동일 패키지 내에서만 수정을 하면 된다
