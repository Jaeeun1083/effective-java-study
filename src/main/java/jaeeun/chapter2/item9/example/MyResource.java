package jaeeun.chapter2.item9.example;

public class MyResource implements AutoCloseable{
    public void doSomething() throws FirstException {
        System.out.println("Do Something");
        throw new FirstException();
    }

    @Override
    public void close() throws Exception {
        System.out.println("close MyResource");
        throw new SecondException();
    }
}
