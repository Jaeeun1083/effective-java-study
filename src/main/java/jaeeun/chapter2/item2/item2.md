## [item 2] 생성자에 매개변수가 많다면 빌더를 고려하라

## 동작 방식
- 클라이언트는 필요한 객체를 직접 만드는 대신 필수 매개변수만으로 생성자(혹은 정적 팩터리)를 호출해 빌더 객체를 얻는다.
- 빌더 객체가 제공하는 세터 메서드들로 원하는 선택 매개변수들을 설정한다.
- 매개변수가 없는 build 메서드를 호출해 객체를 얻는다.

   ```
   public class Product {
     private String name;
     private double price;
     private String description;
     private int stock;

     private Product(Builder builder) {
       this.name = builder.name;
       this.price = builder.price;
       this.description = builder.description;
       this.stock = builder.stock;
     }
  
     // Getter 생략
  
     public static class Builder {
       private String name;
       private double price;
       private String description;
       private int stock;

       public Builder(String name, double price) {
         this.name = name;
         this.price = price;
       }

       public Builder description(String description) {
         this.description = description;
         return this;
       }

       public Builder stock(int stock) {
         this.stock = stock;
         return this;
       }

       public Product build() {
         return new Product(this);
       }
     }
   }
  
    // 사용
    Person person1 = new Person.Builder("Alice", 30).build();
    Person person2 = new Person.Builder("Bob", 25)
                .address("123 Main Street")
                .build();
   ```

#### 메서드 체인이란?
- 메서드 체인(Method Chaining)은 한 줄에 여러 개의 메서드를 연속적으로 호출하는 방식을 말한다.
- 이러한 방식을 사용하면 각 메서드는 자기 자신을 반환하도록 설계되어 있어서, 반환된 객체를 바로 다음 메서드의 호출 대상으로 사용할 수 있다.

#### Lombok의 @Builder 동작 원리
- Lombok의 `@Builder` 어노테이션을 클래스 레벨에 붙이거나 생성자에 적용하면 파라미터를 활용하여 빌더 패턴을 자동으로 생성해준다.

- **클래스 레벨에 적용할 경우** 
  - 클래스에 private 생성자가 자동으로 생성되며, 모든 필드를 인자로 갖는 생성자가 만들어진다. (`@AllArgsConstructor(access = AccessLevel.PRIVATE)`가 적용된 것과 동일한 효과를 가진다.)
  - 이 생성자는 @Builder 어노테이션이 적용된 것처럼 동작한다.
  - 이 때 이미 초기화 된 final 필드는 제외하고 빌더 클래스의 메서드가 구현된다.
    ```
    @Builder
    public class Person {
      private final String name;
      private final int age;
      private final String address;
      private boolean isEmployed = false; // 구현 X
    }
    
    Person person1 = Person.builder()
                    .name("Alice")
                    .age(30)
                    .address("123 Main Street")
                    .isEmployed(true) // 컴파일 오류: isEmployed 필드에 대한 기본값을 설정할 수 없음
                    .build();
    ```
  - 초기화하지 않은 final 필드는 제외하지 않는데 이 때 Lombok은 @Builder 어노테이션이 적용된 클래스에 대해 자동으로 생성되는 빌더 클래스에서 해당 필드에 대한 기본 값을 제공한다.
    ```
    @Builder
    public class Person {
      private String name;
      private int age;
      private String address;
      private final boolean isEmployed;
    }
    
    Person person1 = Person.builder()
            .name("Alice")
            .age(30)
            .address("123 Main Street")
            .build();
    
    System.out.println(person1.isEmployed()); // false
    ```