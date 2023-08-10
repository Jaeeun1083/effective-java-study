###

#### 01. transient 선언과 readResolve 메서드를 구현했을 경우
```
class SerializableClass implements Serializable {
    private static final transient SerializableClass INSTANCE = new SerializableClass();

    private SerializableClass() {
    }

    public static SerializableClass getInstance() {
        return INSTANCE;
    }

    // Implement readResolve to ensure singleton during deserialization
    private Object readResolve() {
        return INSTANCE;
    }
}
```

#### 02. readResolve 메서드만 구현했을 경우
```
class NotSerializableClass01 implements Serializable {
    private static final transient NotSerializableClass01 INSTANCE = new NotSerializableClass01();

    private NotSerializableClass01() {
    }

    public static NotSerializableClass01 getInstance() {
        return INSTANCE;
    }

//  private Object readResolve() {
//      return INSTANCE;
//  }
}
```

#### 03. transient 선언만 했을 경우
```
class NotSerializableClass01 implements Serializable {
    private static final transient NotSerializableClass01 INSTANCE = new NotSerializableClass01();

    private NotSerializableClass01() {
    }

    public static NotSerializableClass01 getInstance() {
        return INSTANCE;
    }

//  private Object readResolve() {
//      return INSTANCE;
//  }
}
```

#### 04. Serializable을 구현할 때 아무 것도 하지 않았을 경우
```
class NotSerializableClass02 implements Serializable {
    private static final NotSerializableClass02 INSTANCE = new NotSerializableClass02();

    private NotSerializableClass02() {
    }

    public static NotSerializableClass02 getInstance() {
        return INSTANCE;
    }

```


```
public class Main {
  public static void main(String[] args) {
    SerializableClass instance1 = SerializableClass.getInstance();
    
    // Serialize the instance
    serializeInstance(instance1);
    
    // Deserialize the instance
    SerializableClass instance2 = deserializeInstance();
    
    System.out.println("Instance 1: " + instance1);
    System.out.println("Instance 2: " + instance2);
    
    System.out.println("Are instances the same? " + (instance1 == instance2));  // Output: true
  }
  
  private static void serializeInstance(SerializableClass instance) {
    try (FileOutputStream fileOut = new FileOutputStream("singleton.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
        out.writeObject(instance);
        System.out.println("Instance serialized");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static SerializableClass deserializeInstance() {
    try (FileInputStream fileIn = new FileInputStream("singleton.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn)) {
        return (SerializableClass) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

}  
```

- 이렇게 예제를 다 넣어서 확인해본 결과 `readResolve()`가 되어 있지 않은 경우 직렬화 / 역직렬화 시 새로운 인스턴스가 만들어 졌다.
- 책에서는 transient 선언도 해야한다고 했는데 왜 그런 건지 잘 모르겠다..