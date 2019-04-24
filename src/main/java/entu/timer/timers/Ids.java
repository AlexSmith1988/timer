package entu.timer.timers;

public class Ids {
    
    private int id;

    public int nextId() {
        return ++id;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
