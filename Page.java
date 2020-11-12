public class Page {
    public String address;
    public int dirtyBit;
    public int RBit;
    public int age;

    public Page(String address) {
        this.address = address;
        this.dirtyBit = 0;
        this.RBit = 0;
        this.age = 0;
    }

    public String toString(){
        return address;
    }
}