package umm3601.todo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.BadRequestResponse;


/**
 * A fake "database" of todo info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of todo data from a specified JSON file, and
 * then provide various database-like methods that allow the `TodoController` to
 * "query" the "database".
 */

 public class DatabaseTD {

  private Todo[] allTodos;

  public DatabaseTD(String todoDataFile) throws IOException {
    Gson gson = new Gson();
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todoDataFile));
    allTodos = gson.fromJson(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }

  public Todo getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

    /**
   * Get an array of all the todos satisfying the queries in the params.
   *
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the todos matching the given criteria
   */
  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;

    // Filter owner if defined
    if (queryParams.containsKey("owner")) {
      String targetOwner = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, targetOwner);
    }
    // Filter category if defined
    if (queryParams.containsKey("category")) {
      String targetCategory = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(filteredTodos, targetCategory);
    }
    // Filter status if defined
    if (queryParams.containsKey("status")) {
      String targetStatus = queryParams.get("status").get(0);
      boolean type= true;
      if(targetStatus.equals("incomplete")) {
        type = false;
      }
      filteredTodos = filterTodosByStatus(filteredTodos, type);
    }
    // filter by limit if defined
    if (queryParams.containsKey("limit")) {
      String targetLimit = queryParams.get("limit").get(0);
      try{
        int intConversion = Integer.parseInt(targetLimit);
        filteredTodos = filterTodosByLimit(filteredTodos, intConversion);
      }
      catch (NumberFormatException e) {
        throw new BadRequestResponse("The specified limit '" + targetLimit +
        "' cannot be converted to an integer");
      }
    }
    // Process other query parameters here...

    return filteredTodos;
  }

  /**
   * Get an array of all the todos having the target owner.
   *
   * @param todos     the list of todos to filter by owner
   * @param targetOwner the target owner to look for
   * @return an array of all the todos from the given list that have the target
   *         owner
   */
  public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  }

  /**
   * Get an array of all the todos having the target category.
   *
   * @param todos         the list of todos to filter by category
   * @param targetCategory the target category to look for
   * @return an array of all the todos from the given list that have the target
   *         category
   */
  public Todo[] filterTodosByCategory(Todo[] todos, String targetCategory) {
    return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  }

  /**
   * Gets an array of all the todos having the target status.
   *
   * @param todos         the list of todos to filter by completion status
   * @param targetStatus  the status type to look for (true or false)
   * @return an array of all the todos from the given list that have the target status type
   */
  public Todo[] filterTodosByStatus(Todo[] todos, boolean targetStatus) {
    return Arrays.stream(todos).filter(x -> x.status == targetStatus).toArray(Todo[]::new);
  }

  /**
   *
   * @param todos       the list of todos to filter by limit
   * @param targetLimit the integer limit for how many todos to display
   * @return a trimmed copy of the array of todos containing the specified limit of todos
   */
  public Todo[] filterTodosByLimit(Todo[] todos, int targetLimit) {
    return Arrays.copyOfRange(todos, 0, targetLimit);
  }
 }
