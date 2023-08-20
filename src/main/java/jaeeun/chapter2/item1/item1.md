## [item 1] 생성자 대신 정적 팩터리 메서드를 고려하라.

### 장점

#### 1. 이름을 가질  수 있다.
- BigInteger(int, int, Random)과 같은 생성자와 비교했을 때 BigInteger.probablePrime 과 같이 역할을 드러내는 이름을 지어줄 수 있다.

#### 2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.
- Boolean.valueOf(boolean)과 같이 반복되는 요청에 같은 객체를 반환하여 인스턴스 통제 클래스를 만들어줄 수 있다.
  ```
  public static final Boolean TRUE = new Boolean(true);
  public static final Boolean FALSE = new Boolean(false);
  
  ...
  
  public static Boolean valueOf(boolean b) {
    return (b ? TRUE : FALSE);
  }
  ```
  - 두 개의 상수 필드 TRUE와 FALSE는 Boolean 클래스에서 **미리 정의된 불변 인스턴스**이다.
  - `Boolean.valueOf(boolean)` 메서드 내부에서는 삼항 연산자를 사용하여 이미 생성된 상수 필드를 반환하게 되고 이렇게 하여 항상 동일한 불변 객체를 반환하므로 반복 호출 시에도 기존의 인스턴스를 재활용할 수 있다.

#### 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.  (클래스 구현의 캡슐화)
- 반환할 객체의 클래스를 자유롭게 선택하여 구현 클래스를 공개하지 않고도 그 객체를 반환할 수 있다.
- 예를 들어 Collections 클래스는 List, Set, Map 등과 같은 컬렉션 객체들을 생성하기 위한 팩터리 메서드들을 제공한다.
  ```
  List<String> emptyList = Collections.emptyList();
  
  public class Collections {
     public static final List EMPTY_LIST = new EmptyList<>();
     ...
  
     public static final <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }
  
     private static class EmptyList<E> extends AbstractList<E> implements RandomAccess, Serializable {
        ...
     }
     ...
  }
  ```
   - EmptyList 클래스는 빈 리스트를 나타내기 위해 아무 요소도 갖지 않고, 변경이 불가능한(immutable) 리스트이다.
   - Collections.emptyList() 메서드는 항상 이 EmptyList 클래스의 인스턴스를 반환하고
   - 이 때 필요한 객체를 매번 새로 생성하는 대신, 하나의 불변 객체를 재사용한다.

#### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
- 어떤 구현 클래스를 반환할지는 정적 팩토리 메서드의 내부 로직에 따라 결정할 수 있고 
- 반환 타입의 하위 타입인 클래스의 객체를 반환할 수 있으므로 클라이언트는 반환 클래스가 바뀌더라도 영향받지 않고 사용할 수 있다.
   ```
   // Shape 인터페이스와 이를 구현하는 Circle과 Rectangle 클래스
   public interface Shape {
     void draw();
   }
   
   public class Circle implements Shape {
     @Override
     public void draw() {
       System.out.println("Drawing a Circle.");
     }
   }
   
   public class Rectangle implements Shape {
     @Override
     public void draw() {
       System.out.println("Drawing a Rectangle.");
     }
   }
  
  
  // 정적 팩터리 메서드를 사용하여 객체를 생성하는 ShapeFactory 클래스
  public class ShapeFactory {
    public static Shape createShape(String shapeType) {
      if ("circle".equalsIgnoreCase(shapeType)) {
        return new Circle();
      } else if ("rectangle".equalsIgnoreCase(shapeType)) {
        return new Rectangle();
      } else {
        throw new IllegalArgumentException("Invalid shape type: " + shapeType);
      }
    }
  }

  // 활용
  public class Client {
    public static void main(String[] args) {
      Shape circle = ShapeFactory.createShape("circle");
      circle.draw(); // Drawing a Circle.

      Shape rectangle = ShapeFactory.createShape("rectangle");
      rectangle.draw(); // Drawing a Rectangle.
    }
  }
  ```
#### 5. 정적 펙터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

### 단점

#### 1. 상속을 하려면 public이나 prodtected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
#### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다. (메서드 이름을 규약에 맞게 작성하여 문제를 완화할 수 있다.)

- `from` : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드
- `of` : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
- `valuOf` : `from`과 `of`의 더 자세한 버전
- `instance / getInstance` : 매개변수를 받는다면 매개변수로 명시한 인스턴스를 반환하지만 같은 인스턴스임을 보장하지는 않는다.
- `create / newInstance` : `instance / getInstance`와 같지만 매번 새로운 인스턴스를 생성해 반환함을 보장한다.
- `getType` : `getInstance`와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다.
- `newType` : `newInstance`와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다.
- `type` : `getType과 newType`의 간결한 버전