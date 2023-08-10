## [item 3] private 생성자나 열거 타입으로 싱글턴임을 보증하라.

- `싱글턴`이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 뜻한다.
### public static final 필드 방식의 싱글턴
  ```
  public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    ...
  }
  ```
  - 이 방식의 장점은 해당 클래스가 싱글턴임이 API에 명백히 드러난다는 것이다.
    - public static 필드가 final이므로 절대 다른 객체를 참조할 수 없다.

### 정적 팩터리 방식의 싱글턴
   ```
   public class Elvis {
     private static final Elvis INSTANCE = new Elvis();
     private Elvis() { ... }
     public static Elvis getInstance() { return INSTANCE; }
     ...
   } 
   ```
   - 항상 **같은 객체의 참조를 반환**한다.
   - 이 방식의 장점은 API를 바꾸지 않고도 유일한 인스턴스를 반환하는 팩터리 메서드가 **다른 인스턴스를 넘겨주게** 할 수 있다.
   - 원한다면 정적 팩터리를 **제네릭 싱글턴 팩터리**로 만들 수 있다.
     ```
     class SingletonFactory<T> {
       private Map<Class<? extends T>, T> instances = new HashMap<>();

       public T getInstance(Class<? extends T> clazz) {
         if (!instances.containsKey(clazz)) {
           try {
             T instance = clazz.getDeclaredConstructor().newInstance();
             instances.put(clazz, instance);
           } catch (Exception e) {
             e.printStackTrace();
           }
         }
         return instances.get(clazz);
       }
     }
     ```
   - 정적 팩터리의 메서드 참조를 **공급자**로 사용할 수 있다.
     ```
     Supplier<Elvis> elvisSupplier = Elvis::getInstance;
     Elvis instance1 = elvisSupplier.get();
     ```

#### 직렬화 시 주의할 점
- 위의 방식 대로 만든 싱글턴 클래스를 직렬화 하려면 Serializable 구현 뿐만 아니라 모든 인스턴스 필드를 (transient)라고 선언하고 readResolve 메서드를 제공해야한다.
- 그렇게 하지 않으면 직렬화된 인스턴스를 역직렬화 할 때마다 새로운 인스턴스가 만들어진다. 근데 예제를 통해 확인해본 결과 transient 선언은 영향을 주지 않았다.. 뭐지

  => [예제로 이해하기](example/serializable.md)