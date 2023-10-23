## [item 23] 태그 달린 클래스보다는 클래스 계층 구조를 활용하라

두 가지 이상의 의미를 표현할 수 있으며, 그중 현재 표현하는 의미를 태그 값으로 알려주는 클래스를 본 적이 있을 것이다. 

다음 코드는 원과 사각형을 표현할 수 있는 클래스다.

```java
class Figure {
    enum Shape { RECTANGLE, CIRCLE };

    // 태그 필드
    final Shape shape;
    
    // 다음 필드들은 모양이 사각형일 때만 쓰인다.
    double length;
    dobule width;
    
    // 다음 필드는 모양이 원일 때만 쓰인다.
    double radius;
    
    // 원용 생성자
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // 사격형용 생성자
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

### 태그 달린 클래스의 단점

- 여러 구현이 한 클래스에 혼합돼 있어서 가독성이 나쁘다. 
- 다른 의미를 위한 코드도 언제나 함께 하니 메모리도 많이 사용한다. 
- 필드들을 final로 선언하려면 해당 의미에 쓰이지 않는 필드들까지 생성자에서 초기화해야 한다. 
- 새로운 의미를 추가할 때마다 모든 switch 문을 찾아 새 의미를 처리하는 코드를 추가해야 하는데, 하나라도 빠뜨리면 역시 런타임에 문제가 불거져 나올 것이다. 
- 인스턴스의 타입만으로는 현재 나타내는 의미를 알 길이 전혀 없다. 즉 **태그 달린 클래스는 장황하고, 오류를 내기 쉽고 비효율적** 이다.

### 서브타이핑 (계층 구조)

해당 태그 달린 클래스의 문제점을 해결하는 수단 중 하나는 클래스 계층 구조를 활용하는 `서브 타이핑(subtyping)`이다.

#### 태그 달린 클래스를 클래스 계층 구조로 바꾸는 방법
- 계층 구조의 루트가 될 추상 클래스를 정의하고, 태그 값에 따라 동작이 달라지는 메서드들을 루트 클래스의 추상 메서드로 선언한다.
- 태그 값에 상관없이 동작이 일정한 메서드들을 루트 클래스의 일반 메서드로 추가한다.
- 하위 클래스에서 공통으로 사용하는 데이터 필드들도 루트 클래스로 올린다.

```java
abstract class Figure {
    abstract double area();
}

class Circle extends Figure {
    final double radius;
    
    Circle(double radius) { this.radius = radius; }
    
    @Override double area() { return Math.PI * (radius * radius); }
}

class Rectangle extends Figure {
    final double length;
    final double width;
    
    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }
    
    @Override double area() { return length * width; }
}
```

- 타입이 의미 별로 따로 존재하니 변수의 의미를 명시하거나 제한할 수 있고, 또 특정 의미만 매개 변수로 받을 수 있다. 
- 또한, 타입 사이의 자연스러운 계층 관계를 반영할 수 있어서 유연성은 물론 컴파일 타임 타입 검사 능력을 높여줄 수 있다.
- 