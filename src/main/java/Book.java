import org.sql2o.*;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;

public class Book {
  private String title;
  private String author;
  private boolean isCheckedOut;
  private int id;
  private int patronId;
  private Timestamp checkedOutDate;

  public static final int MAX_DAYS_CHECKED_OUT = 7;

  public Book(String title, String author) {
    this.title = title;
    this.author = author;
    this.isCheckedOut = false;
  }

  public String getTitle() {
    return this.title;
  }

  public String getAuthor() {
    return this.author;
  }

  public boolean getIsCheckedOut() {
    return this.isCheckedOut;
  }

  public int getId() {
    return this.id;
  }

  public int getPatronId() {
    return this.patronId;
  }

  public Timestamp getCheckedOutDate() {
    return this.checkedOutDate;
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO books (title, author, is_checked_out, checked_out_date) VALUES (:title, :author, :is_checked_out, now())";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("title", this.title)
        .addParameter("author", this.author)
        .addParameter("is_checked_out", this.isCheckedOut)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Book> all() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM books";
      return con.createQuery(sql)
        .addColumnMapping("is_checked_out", "isCheckedOut")
        .addColumnMapping("patron_id", "patronId")
        .addColumnMapping("checked_out_date", "checkedOutDate")
        .executeAndFetch(Book.class);
    }
  }

  @Override
  public boolean equals(Object otherBook) {
    if(!(otherBook instanceof Book)) {
      return false;
    } else {
      Book newBook = (Book) otherBook;
      return this.getTitle().equals(newBook.getTitle()) && this.getId() == newBook.getId();
    }
  }

  public static Book find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM books WHERE id = :id";
      Book book = con.createQuery(sql)
        .addParameter("id", id)
        .addColumnMapping("is_checked_out", "isCheckedOut")
        .addColumnMapping("patron_id", "patronId")
        .addColumnMapping("checked_out_date", "checkedOutDate")
        .executeAndFetchFirst(Book.class);
      return book;
    }
  }

  public void updateIsCheckedOut() {
    Book book = Book.find(this.getId());
    if (book.isCheckedOut) {
      book.isCheckedOut = false;
    } else {
      book.isCheckedOut = true;
    }
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE books SET is_checked_out = :bool";
      con.createQuery(sql)
        .addParameter("bool", book.isCheckedOut)
        .executeUpdate();
    }

  }

  public void deleteBook() {
    try(Connection khan = DB.sql2o.open()) {
      String sql = "DELETE FROM books WHERE id = :id";
      khan.createQuery(sql)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public static Book findByTitle(String title) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM books WHERE title = :title";
      Book book = con.createQuery(sql)
        .addParameter("title", title)
        .addColumnMapping("is_checked_out", "isCheckedOut")
        .addColumnMapping("patron_id", "patronId")
        .addColumnMapping("checked_out_date", "checkedOutDate")
        .executeAndFetchFirst(Book.class);
      return book;
    }
  }

  public static Book findByAuthor(String author) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM books WHERE author = :author";
      Book book = con.createQuery(sql)
        .addParameter("author", author)
        .addColumnMapping("is_checked_out", "isCheckedOut")
        .addColumnMapping("patron_id", "patronId")
        .addColumnMapping("checked_out_date", "checkedOutDate")
        .executeAndFetchFirst(Book.class);
      return book;
    }
  }

  public boolean isDue() {
    Timestamp today = new Timestamp(new Date().getTime());
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(today.getTime());
    cal.add(Calendar.DAY_OF_MONTH, Book.MAX_DAYS_CHECKED_OUT);
    Timestamp dueDate = new Timestamp(cal.getTime().getTime());
    if (dueDate.before(today)) {
      return true;
    } else {
      return false;
    }
  }

}
