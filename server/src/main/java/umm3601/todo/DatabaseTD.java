package umm3601.todo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
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
   * Gets an array of all the todos satisfying the queries in the params.
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
    //Filter by the body's contents if defined
    if (queryParams.containsKey("contains")) {
      String targetContent = queryParams.get("contains").get(0);
      filteredTodos = filterTodosByContent(filteredTodos, targetContent);
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
    if (queryParams.containsKey("orderBy")) {
      String targetOrder = queryParams.get("orderBy").get(0);
      filteredTodos = filterTodosByOrder(filteredTodos, targetOrder);
    }
    // Process other query parameters here...

    return filteredTodos;
  }

  /**
   * Gets an array of all the todos having the target owner.
   *
   * @param todos     the list of todos to filter by owner
   * @param targetOwner the target owner to look for
   * @return an array of all the todos from the given list that have the target owner
   */
  public Todo[] filterTodosByOwner(Todo[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  }

  /**
   * Gets an array of all the todos having the target category.
   *
   * @param todos         the list of todos to filter by category
   * @param targetCategory the target category to look for
   * @return an array of all the todos from the given list that have the target category
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
   * Gets an array of all the todos having the target body content.
   *
   * @param todos         the list of todos to filter by body content
   * @param targetContent the content to look for in each todo's body
   * @return an array of all todos from the given list that have the target content
   */
  public Todo[] filterTodosByContent(Todo[] todos, String targetContent) {
    return Arrays.stream(todos).filter(x -> x.body.contains(targetContent) == true).toArray(Todo[]::new);
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

  /**
   * Gets an array of all the todos in an ordered fashion.
   *
   * @param todos       the list of todos to filter through by order
   * @param targetOrder the attribute which the todos should be ordered by
   * @return an array of all todos from the given list in the specified order.
   */
  public Todo[] filterTodosByOrder(Todo[] todos, String targetOrder){
    Todo[] newOrder = todos;
    // The following switch statement chooses the order from the one specified by the client
    // and runs helper methods where appropriate.
    switch(targetOrder) {
      case "owner":
        newOrder = orderByOwner(todos);
      break;

      case "category":
        newOrder = orderByCategory(todos);
      break;

      case "status":
        newOrder = orderByStatus(todos);
      break;

      case "body":
        newOrder = orderByBody(todos);
      break;

      default:
      break;
    }
    return newOrder;
  }

  /*
  The following section contains four helper methods that are used for sorting the todos
  in different ways.
  */
  private Todo[] orderByOwner (Todo[] todos) {
    Arrays.sort(todos, new Comparator<Todo>() {
      public int compare(Todo firstOwner, Todo secondOwner) {
        return firstOwner.owner.compareTo(secondOwner.owner);
      }
     }
     );
    return todos;
  }

  private Todo[] orderByCategory (Todo[] todos) {
    Arrays.sort(todos, new Comparator<Todo>() {
      public int compare(Todo firstCategory, Todo secondCategory) {
        return firstCategory.category.compareTo(secondCategory.category);
      }
     }
     );
    return todos;
  }

  private Todo[] orderByStatus (Todo[] todos) {
    Arrays.sort(todos, new Comparator<Todo>() {
      public int compare(Todo firstStatus, Todo secondStatus) {
        String fStatus = String.valueOf(firstStatus.status); // Each boolean value is stringified
        String sStatus = String.valueOf(secondStatus.status); // to be compared.
        return fStatus.compareTo(sStatus);
      }
    }
    );
    return todos;
  }

  private Todo[] orderByBody (Todo[] todos) {
    Arrays.sort(todos, new Comparator<Todo>() {
      public int compare(Todo firstBody, Todo secondBody) {
        return firstBody.body.compareTo(secondBody.body);
      }
    }
    );
    return todos;
  }
 }
