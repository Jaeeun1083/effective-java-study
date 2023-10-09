## [item 17] 변경 가능성을 최소화하라.

- 불변 클래스(해당 인스턴스의 내부 값을 수정할 수 없는 클래스)는 가변 클래스보다 설계하고 구현하고 사용하기 쉬우며, 오류가 생길 여지가 적고 안전하다.

### 클래스를 불변으로 만들기 위한 다섯가지 규칙

1. **객체의 상태를 변경하는 메서드(변경자)** 를 제공하지 않는다.
2. **클래스를 확장**할 수 없도록 한다. (final Class)
3. 모든 **필드를 final**로 선언한다.
  - 새로 생성된 인스턴스를 동기화 없이 다른 스레드로 건네도 문제 없게 동작하도록 보장한다.
4. 모든 **필드를 private**으로 선언한다.
  - 가변 객체에 클라이언트가 직접 접근해서 수정하는 것을 막아준다. (내부 값을 변경할 수 있는 객체를 가변 객체라고 한다.)
5. 자신 외에는 내부의 **가변 컴포넌트에 접근**할 수 없도록 한다.
   - 접근자 메소드가 필드를 그대로 반환해서도 안된다.

- <a href="./example/Complex.java">Complex 예제</a>

### 불변 클래스의 인스턴스를 최대한 재활용하자.

```java
public class Complex {
    public static final Complex ZERO = new Complex (0,0);
    public static final Complex ONE = new Complex (1,0);
    
    ... 

}

```

- 불변 객체는 스레드의 영향을 받지 않으니 안심하고 공유할 수 있어 자주 사용되는 인스턴스를 캐싱하여 사용하는 것이 좋다.
- 자유롭게 공유할 수 있다는 점은 방어적 복사가 필요 없다는 것과 이어진다.
  - 복사해봐야 원본과 똑같으니 불변 클래스는 clone 메서드나 복사 생성자를 제공하지 않는 것이 좋다.
- 불변 객체는 그 자체로 실패 원자성을 제공한다.
  - 즉 메서드에서 예외가 발생한 후에도 그 객체는 여전히 메서드 호출 전과 똑같은 유효한 상태이다.

### 불변 클래스의 단점
- 값이 다르면 반드시 독립된 객체로 만들어야 한다. (가짓 수가 많을 경우 비용 문제 발생)
- 원하는 객체를 완성하기까지의 단계가 많고 그 중간 단계 객체들이 모두 버려진다면 성능 문제가 더 불거진다.
  - 이를 대처하기 위해 **다단계 연산을 예측하여 기본 기능으로 제공하는 방법**, **다단계 연산 속도를 높여주는 가변 동반 클래스를 제공하는 방법** 활용할 수 있다.

#### 다단계 연산을 예측하여 기본 기능으로 제공하는 방법
- 다단계 연산이란 여러 단계로 나뉘어 수행되는 계산 과정을 뜻한다.
- 예를 들어 BigInteger 클래스의 모듈러 지수 연산 (ex. modPow)에서는 지수 연산과 모듈러 연산을 처리하는데 
  이러한 메서드를 활용하여 사용자는 중간 결과를 직접 계산하고 관리하는 대신 BigInteger 클래스 내부에서 이러한 다단계 연산을 효율적으로 처리할 수 있다.

#### 다단계 연산 속도를 높여주는 가변 동반 클래스를 제공하는 방법
- 클라이언트의 연산을 예측할 수 없다면 불변 클래스와 쌍을 이루는 가변 동반 클래스를 public 클래스로 제공하는 방법이 있다.
- 이에 해당하는 예시가 String 클래스와 가변 동반 클래승 StringBuilder, StringBuffer 이다.
  ```java
  public final class StringBuilder {
      ... 생략
      @Override
      public StringBuilder append(Object obj) {
          return append(String.valueOf(obj));
      }

      @Override
      @HotSpotIntrinsicCandidate
      public StringBuilder append(String str) {
          super.append(str);
          return this;
      }

      @Override
      @HotSpotIntrinsicCandidate
      public String toString() {
          // Create a copy, don't share the array
          return isLatin1() ? StringLatin1.newString(value, 0, count)
                : StringUTF16.newString(value, 0, count);
      }
  }
  ```
  
### 불변 클래스를 만드는 또 다른 설계 방법 

- 클래스가 불변임을 보장하려면 자신을 상속하지 못하게 하는 final 클래스로 선언하는 것이지만 더 유연한 방법이 있다.
- 모든 생성자를 private 혹은 package-private으로 만들고 public 정적 팩터리를 제공하는 방법이다.

#### 생성자를 private 혹은 package-private으로 만들고 public 정적 팩터리를 제공하는 방법

```java
public final class Complex {
    private final double re;
    private final double im;

    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    ... 생략
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }
}
```
- 패키지 바깥의 클라이언트에서 본 이 불변 객체는 사실상 final이다.
  - public이나 protected 생성자가 없으니 다른 패키지에서는 이 클래스를 확장하는 것이 불가능하기 때문이다.
- 이 방식은 다수의 구현 클래스를 확용한 유연성을 제공하고 다음 릴리스에서 객체 캐싱 기능을 추가해 성능을 끌어올릴 수도 있다.

### 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자

- 객체가 가질 수 있는 상태의 수를 줄이면 그 객체를 예측하기 쉬워지고 오류가 생길 가능성이 줄어든다.
- 그러므로 꼭 변경해야 할 필드를 뺀 나머지 모두를 final로 선언하자.