package jaeeun.chapter2.item9.example;

public class MainRunner {
    public static void main(String[] args) throws Exception {
        try_with_resource();
    }

    /*
    Do Something
    close MyResource
    Exception in thread "main" jaeeun.chapter2.item9.example.SecondException
    at jaeeun.chapter2.item9.example.MyResource.close(MyResource.java:15)
    at jaeeun.chapter2.item9.example.MainRunner.main(MainRunner.java:9)
    */
    public static void try_catch() throws Exception {
        MyResource myResource = new MyResource();
        try {
            myResource.doSomething();
        } finally {
            myResource.close();
        }

    }

    /*
    Do Something
    close MyResource
    Exception in thread "main" jaeeun.chapter2.item9.example.FirstException
    at jaeeun.chapter2.item9.example.MyResource.doSomething(MyResource.java:6)
    at jaeeun.chapter2.item9.example.MainRunner.try_with_resource(MainRunner.java:26)
    at jaeeun.chapter2.item9.example.MainRunner.main(MainRunner.java:5)
    Suppressed: jaeeun.chapter2.item9.example.SecondException
    at jaeeun.chapter2.item9.example.MyResource.close(MyResource.java:12)
    at jaeeun.chapter2.item9.example.MainRunner.try_with_resource(MainRunner.java:25)
            ... 1 more
    */
    public static void try_with_resource() throws Exception {
        try (MyResource myResource = new MyResource()) {
            myResource.doSomething();
        }
    }

}
