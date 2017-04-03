import org.sql2o.*;
import java.util.List;

public class Book {
  private String title;
  private String author;
  private boolean isCheckedOut;
  private int id;
  // private int patronId;

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

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO books (title, author, is_checked_out) VALUES (:title, :author, :is_checked_out)";
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
        .executeAndFetchFirst(Book.class);
      return book;
    }
  }

  public void updateIsCheckedOut() {
    if (this.isCheckedOut) {
      this.isCheckedOut = false;
    } else {
      this.isCheckedOut = true;
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
        .executeAndFetchFirst(Book.class);
      return book;
    }
  }
}
