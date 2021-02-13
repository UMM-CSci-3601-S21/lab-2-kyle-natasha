package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests umm3601.todo.DatabaseTD filterTodosByAge and listTodos with _age_ query
 * parameters
 */
public class FilterTodosByCatFromDBTD {

  @Test
  public void filterTodosByCategory() throws IOException {
    DatabaseTD db = new DatabaseTD("/todos.json");
    Map<String, List<String>> queryParams = new HashMap<>();

    queryParams.put("category", Arrays.asList(new String[] { "software design" }));
    Todo[] categorySDTodos = db.listTodos(queryParams);
    assertEquals(74, categorySDTodos.length, "Incorrect number of todos for software design");

    queryParams.put("category", Arrays.asList(new String[] { "homework" }));
    Todo[] categoryHWTodos = db.listTodos(queryParams);
    assertEquals(79, categoryHWTodos.length, "Incorrect number of todos for homework");
  }
}
