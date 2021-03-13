import java.util.ArrayList;

public class Knapsack {
    private int capacity;
    private ArrayList<Item> items = new ArrayList<>();
    
    public Knapsack(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public int getSize() {
        return items.size();
    }

    public boolean contains(Item i) {
        return items.contains(i);
    }

    public void clear() {
        items.clear();
    }
}
