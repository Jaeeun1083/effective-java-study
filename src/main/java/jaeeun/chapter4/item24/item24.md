## [item 24] 멤버 클래스는 되도록 static으로 만들라

중첩 클래스란 다른 클래스 안에 정의된 클래스를 뜻한다. 중첩 클래스는 자신을 감싼 바깥 클래스에서만 쓰여야 하며, 그 외의 쓰임새가 있다면 톱레벨 클래스로 만들어야 한다.

#### 중첩 클래스의 종류
- 정적 멤버 클래스
- (비정적) 멤버 클래스
- 익명 클래스
- 지역 클래스

### 정적 멤버 클래스 (static)
- 다른 클래스 안에 선언되고, 바깥 클래스의 private 멤버에도 접근할 수 있다는 점만 제외하고는 일반 클래스와 똑같다.
- 흔히 바깥 클래스와 함께 쓰일 때만 유용한 public 도우미 클래스로 쓰인다.

```java
public class OuterClass {
    private int outerField = 10;

    public OuterClass() {
        // OuterClass 생성자
    }

    public void outerMethod() {
        System.out.println("Outer Method");
    }

    public static class StaticInnerClass {
        public void innerMethod() {
            System.out.println("StaticInnerClass Inner Method");
        }

        public void accessOuterField(OuterClass outer) {
            System.out.println("Accessing outerField from inner class: " + outer.outerField);
        }
    }
}
```

### 비정적 멤버 클래스 (non-static)
- 비정적 멤버 클래스의 인스턴스는 바깥 클래스의 인스턴스와 암묵적으로 연결된다.
- 그래서 비정적 멤버 클래스의 인스턴스 메서드에서 `정규화된 this`를 사용해 바깥 인스턴스의 메서드를 호출하거나 바깥 인스턴스의 참조를 가져올 수 있다. 
  - 정규화된 this : 클래스명.this 형태로 바깥 클래스의 이름을 명시하는 용법
- **멤버 클래스에서 바깥 인스턴스에 접근할 일이 없다면 무조건 static을 붙여서 정적 멤버 클래스로 만들자** 
  - static을 생략하면 바깥 인스턴스로의 숨은 외부 참조를 갖게 된다. (이 참조를 저장하려면 시간과 공간이 소비된다.)
  - 가비지 컬렉션이 바깥 클래스의 인스턴스를 수거하지 못하는 메모리 누수가 생길 수 있다.
```java
public class OuterClass {
    private int outerField = 10;

    public void outerMethod() {
        System.out.println("Outer Method");
    }

    // 비정적 멤버 클래스
    public class InnerClass {
        public void innerMethod() {
          System.out.println("InnerClass Inner Method");

          // 비정적 멤버 클래스에서 바깥 클래스의 메서드 호출
          outerMethod();

          // 정규화된 this를 통한 바깥 클래스 참조
          OuterClass.this.outerMethod();
        }

        // 바깥 클래스의 필드에 접근
        public void accessOuterField() {
          System.out.println("Accessing outerField from inner class: " + OuterClass.this.outerField);
        }
    }
}
```

### 익명 클래스
- 흔히 바깥 클래스가 표현하는 객체의 한 부분 (구성요소)을 나타낼 때 쓴다.
- 익명 클래스는 바깥 클래스의 멤버가 아니다. 멤버와 달리 쓰이는 시점에 선언과 동시에 인스턴스가 만들어진다.
- 비정적인 문맥에서 사용될 때만 바깥 클래스의 인스턴스를 참조할 수 있다. 정적 문맥에서라도 상수 변수 이외의 정적 멤버는 가질 수 없다. 즉 상수 표현을 위해 초기화된 final 기본 타입과 문자열 필드만 가질 수 있다. (무슨 말이지..)
- 익명 클래스를 사용하는 클라이언트는 그 익명 클래스가 상위 타입에서 상속한 멤버 외에는 호출할 수 없다. ??
```java
interface Greeting {
    void greet();
}

public class MainRunner {
    public static void main(String[] args) {
        // Greeting 인터페이스를 구현하는 익명 클래스
        Greeting greeting = new Greeting() {
            @Override
            public void greet() {
                System.out.println("Hello");
            }
        };

        Greeting greetingWithLambda = () -> System.out.println("Hello");
        // 익명 클래스의 메서드 호출
        greeting.greet();
    }
}
```

### 지역 클래스
- 지역 변수를 선언할 수 있는 곳이면 어디서든 선언할 수 있고, 유효 범위도 지역변수와 같다.

> 핵심 정리
> 
> 메서드 밖에서도 사용해야 하거나 메서드 안에 정의하기엔 너무 길다면 멤버 클래스로 만든다.
> 
> 멤버 클래스의 인스턴스 각각이 바깥 인스턴스를 참조한다면 비정적으로 그렇지 않다면 정적으로 만들자.
> 
> 중첩 클래스가 한 메서드 안에서만 쓰이면서 그 인스턴스를 생성하는 지점이 단 한 곳이고 해당 타입으로 쓰기에 적합한 클래스나 인터페이스가 있다면 익명 클래스로 만들고 그렇지 않으면 지역 클래스로 만들자.