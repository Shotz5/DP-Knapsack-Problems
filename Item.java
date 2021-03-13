public class Item {
    private int itemID;
    private int weight;
    private int price;

    public Item(int itemID, int weight, int price) {
        this.itemID = itemID;
        this.weight = weight;
        this.price = price;
    }

    public int getItemID() {
        return itemID;
    }

    public int getWeight() {
        return weight;
    }

    public int getPrice() {
        return price;
    }
}