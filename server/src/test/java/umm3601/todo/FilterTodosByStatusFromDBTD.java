package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests umm3601.todo.DatabaseTD filterTodosByOwner and listTodos with _owner_ query
 * parameters
 */
public class FilterTodosByStatusFromDBTD {

  @Test
  public void filterTodosByStatus() throws IOException {
    DatabaseTD db = new DatabaseTD("/todos.json");
    Todo[] allTodos = db.listTodos(new HashMap<>());

    Todo[] TrueTodos = db.filterTodosByStatus(allTodos, true);
    assertEquals(143, TrueTodos.length, "Incorrect number of todos that are complete");

    Todo[] FalseTodos = db.filterTodosByStatus(allTodos, false);
    assertEquals(157, FalseTodos.length, "Incorrect number of todos that are incomplete");
  }
}
