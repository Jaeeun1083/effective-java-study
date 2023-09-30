## [item 15] 클래스와 멤버의 접근 권한을 최소화하라.

- 잘 설계된 컴포넌트와 그렇지 못한 컴포넌트의 가장 큰 차이는 클래스 내부 데이터와 구현 정보를 외부로부터 얼마나 잘 숨겼느냐로 결정된다.
- 잘 설계된 컴포넌트는 내부 구현을 완벽히 숨겨 구현과 API를 분리한다. (= `정보은닉`, `캠슐화`)
  - 이는 사용자 입장에서 API만 싱경쓰면 되기 때문이다.

### 캡슐화(정보 은닉)의 장점

- **시스템 개발 속도** 를 높인다. 
  - 여러 컴포넌트를 병렬로 개발할 수 있기 때문이다.
- **시스템 관리 비용**을 낮춘다. 
  - 각 컴포넌트를 더 빨리 파악하여 디버깅할 수 있고, 다른 컴포넌트로 교체하는 부담도 적기 때문이다.
- **성능 최적화에 도움**을 준다. 
  - 완성된 시스템을 프로파일링 해 최적화할 컴포넌트를 정한 후 다른 컴포넌트에 영향을 주지 않고 해당 컴포넌트만 최적화 할 수 있기 때문이다.
- **소프트웨어 재사용성**을 높인다. 
  - 의존성이 낮은 컴포넌트라면 그 컴포넌트와 함께 개발되지 않은 낯선 환경에서도 유용하게 쓰일 가능성이 크기 때문이다. 
- 큰 시스템을 **제작하는 난이도**를 낮춰준다.
  - 개별 컴포넌트의 동작을 검증할 수 있기 때문이다.

### 자바의 캡슐화(정보 은닉)를 위한 장치
- 접근 제어 메커니즘을 이용해 클래스, 인터페이스, 멤버의 접근성(접근 허용 범위)을 명시한다.
- 각 요소의 접근성은 그 요소가 선언된 위치와 접근 제한자로 정해진다. 이 **접근 제한자를 활용**하는 것이 핵심이다.

#### 멤버(필드, 메서드, 중첩 클래스, 중첩 인터페이스)에 부여할 수 있는 접근 수준
- private : 멤버를 선언한 톱 레벨 클래스에서만 접근할 수 있다.
- package-private : 멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다.
- protected : package-private의 접근 범위를 포함하며 이 멤버를 선언한 클래스의 하위 클래스에서도 접근할 수 있다.
- public : 모든 곳에서 접근할 수 있다.

### 모든 클래스와 멤버의 접근성을 가능한 좁히자
- **톱 레벨 클래스와 인터페이스**는 `package-private(기본 값)`, `public` 접근 수준을 부여할 수 있다.
  - package-private : 해당 패키지 안에서만 이용 가능 / public : 공개 API
- 패키지 외부에서 쓸 이유가 없다면 package-private로 선언하자.
  - API가 아닌 내부 구현이 되어 클라이언트에 피해 없이 수정, 교체, 제거가 가능하다.
    
> 패키지 구조가 변경될 때마다 접근제한자가 발목을 잡을 가능성이 매우 높다는 점에서 package-private을 사용하는 것에 대해 의견이 나뉘는 것 같다.
> 
> 참고 : <a href="https://hyeon9mak.github.io/Java-dont-use-package-private/"> package private은 안쓰나요?</a>

### private static으로 중첩시켜보자.
- package-private 톱레벨 클래스나 인터페이스가 한 클래스에서만 사용된다면 클래스 안에 private static으로 중첩시켜 바깥 클래스 하나에서만 접근하게 할 수 있다.

```java
public class Device {
    List<Resource> resources;
    
    /*
     * 중첩 클래스 (Static Nested Class)
     * 클래스 선언 안에 또 다른 클래스 선언이 있는 상태.
     * 정적 멤버로 등록된 중첩 클래스
     * */
    private static class Resource {
        String uri;
    }
}
```

### public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다.

- public 가변 필드를 갖는 클래스는 필드가 수정될 때 막을 수 없기에 public 가변 필드를 갖는 클래스는 일반적으로 스레드 안전하지 않다.
- 해당 클래스가 표현하는 추상 개념을 완성하는 데 꼭 필요한 구성요소로써의 상수라면 public static final 필드로 공개해도 좋다.
  - 이런 필드는 반드시 기본 타입 값이나 불변 객체를 참조해야 한다.
- 길이가 0이 아닌 배열은 변경 가능하므로 public static final 배열 필드를 두거나 이 필드를 반환하는 접근자 메서드를 제공하면 안된다.
  1. private 배열과 public 불변 리스트를 추가하는 방법
    ```
    // public 불변 리스트 추가
    private static final Thing[] PRIVATE_VALUES = {...};
    public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));
    ```
  2. private 배열과 그 복사본을 반환하는 public 메서드를 추가하는 방법 (방어적 복사)
    ```
    private static final Thing[] PRIVATE_VALUES = {...};
    public static final Thing[] values() {
      return PRIVATE_VALUES.clone();
    }
    ```

### 모듈 시스템 (자바 9 이후)

- 자바 9에서는 모듈 시스템이라는 개념이 도입되면서 두 가지 암묵적 접근 수준이 추가되었다.
- 모듈은 자신에 속하는 패키지 중 공개 (export)할 것들을 (관례상 module-info.java에) 선언한다. protected 또는 public 멤버라도 해당 패키지를 공개하지 않았다면 모듈 외부에서는 접근할 수 없다.
- 모듈 시스템을 활용하면 클래스를 외부에 공개하지 않으면서도 같은 모듈을 이루는 패키지 사이에 자유롭게 공유할 수 있다.

#### 자바 9 이후의 접근 제한자
- private, package-private, protected는 이전과 동일하다.
- 모듈 내부의 public : 모듈 내부의 모든 곳에서 접근이 가능하다.
- **required의 public** : 모듈에 종속하는 모듈의 모든 패키지 내의 클래스에 접근할 수 있다.
- **export public** : module-info.java에서 제공하는 모든 public에 접근할 수 있다.
