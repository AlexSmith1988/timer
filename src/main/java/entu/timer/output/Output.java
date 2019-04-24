package entu.timer.output;

public interface Output {

    default void print(final String message) {
        System.out.println(message);
    }
}
