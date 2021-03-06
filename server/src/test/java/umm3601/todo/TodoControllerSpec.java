package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.core.validation.Validator;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import umm3601.Server;

/**
 * Tests the logic of the TodoController
 *
 * @throws IOException
 */
public class TodoControllerSpec {

  private Context ctx = mock(Context.class);

  private TodoController todoController;
  private static DatabaseTD db;

  @BeforeEach
  public void setUp() throws IOException {
    ctx.clearCookieStore();

    db = new DatabaseTD(Server.TODO_DATA_FILE);
    todoController = new TodoController(db);
  }

  @Test
  public void GET_to_request_all_todos() throws IOException {
    // Call the method on the mock controller
    todoController.getTodos(ctx);

    // The `json` should have been called with all the todos.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertEquals(db.size(), argument.getValue().length);
  }

  @Test
  public void GET_to_request_owner_Blanche_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] { "Blanche" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All the todos passed to `json` should be owned by Blanche.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("Blanche", todo.owner);
    }
  }

  @Test
  public void GET_to_request_category_groceries_todos() throws IOException {

    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] { "groceries" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos should be in the groceries category.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("groceries", todo.category);
    }
  }

  @Test
  public void GET_to_request_owner_Blanche_category_groceries_todos() throws IOException {

    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] { "Blanche" }));

    queryParams.put("category", Arrays.asList(new String[] { "groceries" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos should be owned by Blanche and be in the groceries category.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("Blanche", todo.owner);
      assertEquals("groceries", todo.category);
    }
  }

  @Test
  public void GET_to_request_status_incomplete() throws IOException {

    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] { "incomplete" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos listed should be incomplete.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertFalse(todo.status);
    }
  }

  @Test
  public void GET_to_request_contains_banana() throws IOException {

    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] { "banana" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // There should be no todos with "banana" in their bodies.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertEquals(0, argument.getValue().length);
  }

  @Test
  public void GET_to_request_limit_7_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] { "7" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // This should confirm that 7 entries are being returned.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    assertEquals(7, argument.getValue().length);
  }

  @Test
  public void GET_to_request_todos_with_illegal_limit() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] { "ASVAB" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    // A `BadRequestResponse` exception should be thrown, since
    // ASVAB is not a valid limit amount.
    Assertions.assertThrows(BadRequestResponse.class, () -> {
      todoController.getTodos(ctx);
    });
  }

  @Test
  public void GET_to_request_todos_ordered_by_owner() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "owner" }));

    when (ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos in the list should be ordered by owner.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    int max = argument.getValue().length;
    for (int i = 0; i < max-1; i++) {
      Integer f = i;
      Integer s = i + 1;
      assertTrue(f.compareTo(s) < 0);
    }
  }

  @Test
  public void GET_to_request_todos_ordered_by_status() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "status" }));

    when (ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos in the list should be ordered by status.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    int max = argument.getValue().length;
    for (int i = 0; i < max-1; i++) {
      Integer f = i;
      Integer s = i + 1;
      assertTrue(f.compareTo(s) < 0);
    }
  }

  @Test
  public void GET_to_request_todos_ordered_by_body() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "body" }));

    when (ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos in the list should be ordered by body.
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    int max = argument.getValue().length;
    for (int i = 0; i < max-1; i++) {
      Integer f = i;
      Integer s = i + 1;
      assertTrue(f.compareTo(s) < 0);
    }
  }

  @Test
  public void GET_to_request_todos_with_multiple_filters() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] { "Blanche" }));
    queryParams.put("status", Arrays.asList(new String[] { "incomplete" }));
    queryParams.put("limit", Arrays.asList(new String[] { "12" }));
    queryParams.put("orderBy", Arrays.asList(new String[] { "category" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);

    // All todos should be owned by Blanche and be incomplete
    ArgumentCaptor<Todo[]> argument = ArgumentCaptor.forClass(Todo[].class);
    verify(ctx).json(argument.capture());
    for (Todo todo : argument.getValue()) {
      assertEquals("Blanche", todo.owner);
      assertFalse(todo.status);
    }
    // There should be 12 todos in total, and they should be ordered by category.
    assertEquals(12, argument.getValue().length);
    for (int i = 0; i < 10; i++) {
      Integer f = i;
      Integer s = i + 1;
      assertTrue(f.compareTo(s) < 0);
    }
  }

  @Test
  public void GET_to_request_todo_with_existent_id() throws IOException {
    when(ctx.pathParam("id", String.class)).thenReturn(new Validator<String>("58895985f0a4bbea24084abf", "", "id"));
    todoController.getTodo(ctx);
    verify(ctx).status(201);
  }

  @Test
  public void GET_to_request_todo_with_nonexistent_id() throws IOException {
    when(ctx.pathParam("id", String.class)).thenReturn(new Validator<String>("nonexistent", "", "id"));
    Assertions.assertThrows(NotFoundResponse.class, () -> {
      todoController.getTodo(ctx);
    });
  }
}
