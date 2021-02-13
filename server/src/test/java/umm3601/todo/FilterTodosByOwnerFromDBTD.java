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
public class FilterTodosByOwnerFromDBTD {

  @Test
  public void filterTodosByOwner() throws IOException {
    DatabaseTD db = new DatabaseTD("/todos.json");
    Todo[] allTodos = db.listTodos(new HashMap<>());

    Todo[] FryTodos = db.filterTodosByOwner(allTodos, "Fry");
    assertEquals(61, FryTodos.length, "Incorrect number of todos owned by Fry");

    Todo[] BlancheTodos = db.filterTodosByOwner(allTodos, "Blanche");
    assertEquals(43, BlancheTodos.length, "Incorrect number of todos owned by Blanche");
  }
}
