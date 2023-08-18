## [item 6] 불필요한 객체 생성을 피하라

- 기능적으로 동일한 객체가 반복해서 필요하다면 새로 만드는 대신 객체 하나를 재사용하는 것이 좋다.

### 문자열 객체 생성

- 생성자를 통해 String 객체를 생성할 경우 항상 새로운 객체를 만들게 되므로 String a = ""; 방식으로 객체를 생성하는 것이 좋다.
- 문자열 리터럴을 재사용하기 때문에 해당 자바 가상 머신에 동일한 문자열이 존재한다면 그 리터럴을 재사용한다.
  ```
  String name1 = new String("jaeeun");
  String name2 = new String("jaeeun");
  System.out.println(name1 == name2); // false

  String name3 = "jaeeun";
	String name4 = "jaeeun";
	System.out.println(name1 == name2); // true
  ```

### static 팩토리 메소드 사용하기

```
Boolean true1 = Boolean.valudOf("true");
Boolean true2 = Boolean.valudOf("true");

System.out.println(true1 == true2); // true
System.out.println(true1 == Boolean.TRUE); // true
```

### 생성 비용이 비싼 객체 생성

- 만드는 데 메모리나 시간이 오래 걸리는 객체를 반복적으로 만들어야한다면 캐싱하여 재사용하는 것을 고려하는 것이 좋다.
- 책에 나온 String.matches 메소드를 살펴보자.

  ```
  static boolean validPassword (String input) {
    String passwordPattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\\\\$%^&*]).{8,20}$"; // 유효성을 검사할 정규표현식 패턴

    return matcher.matches(passwordPattern);
  }
  ```

- Pattern 인스턴스는 한 번 쓰고 버려져 가비지 컬렉션의 대상이 된다. Pattern은 입력받은 정규 표현식에 해당하는 유한 상태 머신을 만들기 때문에 인스턴스 생성 비용이 높다.

- 성능을 개선하기 위해서는 Pattern 객체를 만들어 재사용하는 것이 좋다.

  ```
  public class Password {
    private static final Pattern PW_PATTERN = Pattern.complie("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\\\\$%^&*]).{8,20}$");

    static boolean isValid(String input) {
      return PW_PATTERN.matcher(input).matches();
    }
  }
  ```
- 그러나 이 경우에도 문제점이 있는데 isValid 메소드가 호출되지 않는다면 PW_PATTERN 이 필요 없이 만들어진 셈이 된다.

### 어댑터
- 객체가 불변이라면 재사용해도 안전함이 명백하지만 덜 명확하거나 직관에 반대 되는 상황이 있다. 그 예로는 어댑터가 있다.
- 어댑터는 실제 작업은 뒷단 객체에 위임하고 자신은 제 2의 인터페이스 역할을 해주는 객체이다.

- Map 인터페이스가 제공하는 KeySet은 Map이 뒤에 있는 Set 인터페이스의 뷰를 제공한다. KeySet을 호출할 때마다 같은 객체를 리턴하기 때문에 리턴 받은 Set 타입의 객체를 변경하면 결국은 그 뒤에 있는 Map 객체를 변경하게 된다.
  ```
  Map<String, Integer> menu = new HashMap<>();
  menu.put("Burger", 8);
  menu.put("Pizza", 9);

  Set<String> names1 = menu.keySet();
  Set<String> names2 = menu.keySet();

  // 재사용하는 전역에서 사용하는 Map일 경우 다른 쪽에도 영향을 줄 수 있다
  names1.remove("Burger");
  System.out.println(names2.size()); // 1
  System.out.println(menu.size()); // 1
  ```

### 오토박싱
- 불필요한 객체를 생성하는 또 다른 방법으로 오토박싱이 있다. (프로그래머가 프리미티브 타입과 레퍼런스 타입을 섞어 쓸 수 있게 해주고 박싱과 언박싱을 자동으로 해준다.)

```
long start = System.currentTimeMillis();
Long sum = 0l;
for (long i = 0 ; i <= Integer.MAX_VALUE ; i++) {
  sum += i;
}
System.out.println(sum);
System.out.println(System.currentTimeMillis() - start);
```
- Long 레퍼런스 타입의 sum이 있는데 여기에 long 프리미티브 타입의 i를 계속 더하는 코드이다.
- sum 변수의 타입을 Long으로 만들었기 때문에 불필요한 Long 객체를 2의 31 제곱개 만큼 만들게 되고 대략 6초 조금 넘게 걸린다. 타입을 프리미티브 타입으로 바꾸면 600 밀리초로 약 10배 이상의 차이가 난다.
- 불필요한 오토박싱을 피하려면 레퍼런스 타입 보다는 프리미티브 타입을 사용해야 한다.


*그러나 객체 생성이 비싸므로 피해야한다는 것은 아니다. 아이템 50에서 좀 더 자세히 살펴본다고 한다.*