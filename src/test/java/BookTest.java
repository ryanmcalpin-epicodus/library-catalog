import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class BookTest {

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/library_catalog_test", null, null);
  }

  @After
  public void tearDown() {
    try (Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM books *;";
      con.createQuery(sql).executeUpdate();
    }
  }

  @Test
  public void Book_instantiatesCorrectly_true() {
    Book book = new Book("Title", "Author");
    assertTrue(book instanceof Book);
  }

  @Test
  public void getters_returnsCorrectly_true() {
    Book book = new Book("Title", "Author");
    assertEquals("Title", book.getTitle());
    assertEquals("Author", book.getAuthor());
    assertEquals(false, book.getIsCheckedOut());
  }

  @Test
  public void save_savesToDB_true() {
    Book book = new Book("Title", "Author");
    book.save();
    assertTrue(Book.all().get(0).equals(book));
  }

  @Test
  public void find_returnsById_book2() {
    Book book1 = new Book("Title", "Author");
    book1.save();
    Book book2 = new Book("Titlez", "Authorz");
    book2.save();
    assertEquals(book2, Book.find(book2.getId()));
  }

  @Test
  public void updateIsCheckedOut_changeCheckedOutStatus_true() {
    Book book = new Book("Title", "Author");
    book.save();
    book.updateIsCheckedOut();
    assertEquals(true, book.getIsCheckedOut());
  }

  @Test
  public void deleteBook_deletesBook_true() {
    Book book = new Book("Title", "Author");
    book.save();
    book.deleteBook();
    assertEquals(null, book.find(book.getId()));
  }

  @Test
  public void findByTitle_returnsBookWithMatchingTitle_true() {
    Book book = new Book("Title", "Author");
    book.save();
    Book book2 = new Book("Fresh", "Me");
    book2.save();
    assertEquals(book2, Book.findByTitle(book2.getTitle()));
  }

  @Test
  public void findByAuthor_returnsBookByAuthor_true() {
    Book book = new Book("Title", "Author");
    book.save();
    Book book2 = new Book("Fresh", "Me");
    book2.save();
    assertEquals(book2, Book.findByAuthor(book2.getAuthor()));
  }

}
