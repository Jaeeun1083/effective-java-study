package ka.jaeeun.chapter2.item3.example;

interface Animal {
    String getName();
    void setName(String name);
}

class Dog implements Animal {
    private String name;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

class Main {
    public static void main(String[] args) {
        SingletonAnimal();
    }

    public static void SingletonAnimal() {
        SingletonFactory<Animal> factory = new SingletonFactory<>();

        Animal dog = factory.getInstance(Dog.class);
        dog.setName("maru");
        System.out.println(dog.getName()); // output : maru

        Animal another_dog = factory.getInstance(Dog.class);
        another_dog.setName("dockey"); // output : dockey
        System.out.println(another_dog.getName());

        System.out.println(dog.getName());// output : maru

        System.out.println(dog == another_dog);  // Output: true
    }

}