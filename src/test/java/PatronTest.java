import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.sql.Timestamp;
import java.util.Date;

public class PatronTest {

  @Before
  public void setUp() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/library_catalog_test", null, null);
  }

  @After
  public void tearDown() {
    try (Connection con = DB.sql2o.open()) {
      String sql = "DELETE FROM patrons *;";
      con.createQuery(sql).executeUpdate();
    }
  }

  @Test
  public void patron_instantiatesCorrectly_true() {
    Patron patron = new Patron("Jerry");
    assertTrue(patron instanceof Patron);
  }

  @Test
  public void getters_returnsCorrectly_true() {
    Patron saint = new Patron("Jerry");
    assertEquals(saint.getName(), "Jerry");
  }

  @Test
  public void save_savesToDB_true() {
    Patron patron = new Patron("Jerry");
    patron.save();
    assertTrue(Patron.all().get(0).equals(patron));
  }

  @Test
  public void find_returnsById_patron2() {
    Patron patron = new Patron("Jerry");
    patron.save();
    Patron patron2 = new Patron("Cupcake");
    patron2.save();
    assertEquals(patron2, Patron.find(patron2.getId()));
  }

  @Test
  public void checkOut_checksBookOutToPatron_true() {
    Book book = new Book("Title", "Author");
    book.save();
    Patron patron = new Patron("Jerry");
    patron.save();
    patron.checkOut(book.getId());
    assertTrue(Book.find(book.getId()).getIsCheckedOut());
    assertEquals(patron.getId(), Book.find(book.getId()).getPatronId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void atMaxBooks_recognizesIfPatronHasCheckedOutMaxBooks_true() {
    Patron patron = new Patron("Jerry");
    for(int i = 0; i <= Patron.MAX_BOOKS; i++) {
      Book book = new Book("Title", "Author");
      book.save();
      patron.checkOut(book.getId());
    }
    assertEquals(true, patron.atMaxBooks());
  }



  // @Test
  // public void dude date is two weeks past checkOut
  //   new book
  //   var = book timestamp + two weeks
  //   assert equal(book past due date)

}
