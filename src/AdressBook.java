
public class AdressBook {
    private String bookName;
    private int id;

    public AdressBook(int id, String bookName) {
        this.id = id;
        this.bookName = bookName;
    }

    public int getId() {
        return id;
    }

    public String getBookName() {
        return bookName;
    }
}
