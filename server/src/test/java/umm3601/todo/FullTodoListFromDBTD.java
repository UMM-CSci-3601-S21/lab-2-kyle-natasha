package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class FullTodoListFromDBTD {

  @Test
  public void totalTodoCount() throws IOException {
    DatabaseTD db = new DatabaseTD("/todos.json");
    Todo[] allTodos = db.listTodos(new HashMap<>());
    assertEquals(300, allTodos.length, "Incorrect total number of todos");
  }

}
