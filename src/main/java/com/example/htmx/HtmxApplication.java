package com.example.htmx;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class HtmxApplication {

  public static void main(String[] args) {
    SpringApplication.run(HtmxApplication.class, args);
  }

}

@Controller
@RequestMapping("/todos")
class TodoController {

  private final Set<Todo> todos = new ConcurrentSkipListSet<>(Comparator.comparing(Todo::id));

  TodoController() {
    Arrays.stream("test1,test2,test3".split(","))
        .map(Todos::todo)
        .forEach(this.todos::add);
  }

  @GetMapping
  String todos(Model model) {
    model.addAttribute("todos", this.todos);
    return "todos";
  }

  @ResponseBody
  @DeleteMapping(produces = MediaType.TEXT_HTML_VALUE, path = "/{todoId}")
  String delete(@PathVariable Integer todoId) {
    this.todos.stream()
        .filter(t -> t.id().equals(todoId))
        .forEach(this.todos::remove);
    return "";
  }

  @PostMapping
  HtmxResponse add(@RequestParam("new-todo") String newTodo, Model model) {
    this.todos.add(Todos.todo(newTodo));
    model.addAttribute("todos", this.todos);
    return HtmxResponse.builder()
        .view("todos :: todos")
        .view("todos :: todos-form")
        .build();
  }
}

record Todo(Integer id, String title) {

}

class Todos {

  private static final AtomicInteger id = new AtomicInteger(0);

  static Todo todo(String title) {
    return new Todo(id.incrementAndGet(), title);
  }
}
