package ka.jaeeun.chapter2.item3.example;

import java.util.HashMap;
import java.util.Map;

public class SingletonFactory<T> {
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
