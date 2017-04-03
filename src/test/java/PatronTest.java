import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

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

}
