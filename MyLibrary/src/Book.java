public class Book {
    private int bookId;
    private String title;
    private String author;
    private int readStatus; // 0 = read, 1 = currently reading, 2 = unread

    // Default constructor
    public Book() {
    }

    // Constructor with parameters
    public Book(int bookId, String title, String author, int readStatus) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.readStatus = readStatus;
    }

    // Getters
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getReadStatus() {
        return readStatus;
    }

    // Setters
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    // Utility methods
    public String getReadStatusText() {
        switch (readStatus) {
            case 0: return "Read";
            case 1: return "Currently Reading";
            case 2: return "Unread";
            default: return "Unknown";
        }
    }

    public boolean isRead() {
        return readStatus == 0;
    }

    public boolean isCurrentlyReading() {
        return readStatus == 1;
    }

    public boolean isUnread() {
        return readStatus == 2;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", readStatus=" + readStatus +
                " (" + getReadStatusText() + ")" +
                '}';
    }

}