import org.sql2o.*;
import java.util.List;

public class Patron {
  private String name;
  private int id;
  private int booksAmount;

  public static final int MAX_BOOKS = 5;

  public Patron(String name) {
    this.name = name;
    this.booksAmount = 0;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public int getBooksAmount() {
    return booksAmount;
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO patrons (name) VALUES (:name)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static List<Patron> all() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM patrons";
      return con.createQuery(sql)
      .executeAndFetch(Patron.class);
    }
  }

  @Override
  public boolean equals(Object otherPatron) {
    if(!(otherPatron instanceof Patron)) {
      return false;
    } else {
      Patron newPatron = (Patron) otherPatron;
      return this.getName().equals(newPatron.getName()) && this.getId() == newPatron.getId();
    }
  }

  public static Patron find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT * FROM patrons WHERE id = :id";
      Patron patron = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Patron.class);
      return patron;
    }
  }

  public void checkOut(int bookId) {
    if (this.atMaxBooks()) {
      throw new IllegalArgumentException("You've already checked out the limit of " + Patron.MAX_BOOKS + " books.");
    }
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE books SET patron_id = :patron_id WHERE id = :id";
      con.createQuery(sql)
        .addParameter("patron_id", this.id)
        .addParameter("id", bookId)
        .executeUpdate();
      Book.find(bookId).updateIsCheckedOut();
    }
    this.addBook();
  }

  public boolean atMaxBooks() {
    if (this.booksAmount >= MAX_BOOKS) {
      return true;
    } else {
      return false;
    }
  }

  public void addBook() {
    this.booksAmount++;
  }

}
