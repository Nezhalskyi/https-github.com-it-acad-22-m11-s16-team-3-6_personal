package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.security.Utilities;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

    public ToDoController(ToDoService todoService, TaskService taskService, UserService userService) {
        this.todoService = todoService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, Model model) throws AccessDeniedException {
        if (Utilities.getUserDetails().getId() != ownerId && !Utilities.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        model.addAttribute("todo", new ToDo());
        model.addAttribute("ownerId", ownerId);
        model.addAttribute("userName", Utilities.getUserName());
        model.addAttribute("userId", Utilities.getUserDetails().getId());
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String create(@PathVariable("owner_id") long ownerId, @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) throws AccessDeniedException {
        if (Utilities.getUserDetails().getId() != ownerId && !Utilities.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        if (result.hasErrors()) {
            return "create-todo";
        }
        todo.setCreatedAt(LocalDateTime.now());
        todo.setOwner(userService.readById(ownerId));
        todoService.create(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{id}/tasks")
    public String read(@PathVariable long id, Model model) throws AccessDeniedException {
        ToDo todo = todoService.readById(id);
        if (!todoService.getByUserId(Utilities.getUserDetails().getId()).contains(todo) && !Utilities.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        List<Task> tasks = taskService.getByTodoId(id);
        List<User> users = userService.getAll().stream()
                .filter(user -> user.getId() != todo.getOwner().getId()).collect(Collectors.toList());
        users.removeAll(todo.getCollaborators());
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
        model.addAttribute("userName", Utilities.getUserName());
        model.addAttribute("userId", Utilities.getUserDetails().getId());
        return "todo-tasks";
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId, Model model) throws AccessDeniedException {
        if (Utilities.getUserDetails().getId() != ownerId && !Utilities.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        ToDo todo = todoService.readById(todoId);
        model.addAttribute("todo", todo);
        model.addAttribute("userName", Utilities.getUserName());
        model.addAttribute("userId", Utilities.getUserDetails().getId());
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String update(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId,
                         @Validated @ModelAttribute("todo") ToDo todo, BindingResult result) {
        if (result.hasErrors()) {
            todo.setOwner(userService.readById(ownerId));
            return "update-todo";
        }
        ToDo oldTodo = todoService.readById(todoId);
        todo.setOwner(oldTodo.getOwner());
        todo.setCollaborators(oldTodo.getCollaborators());
        todoService.update(todo);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) throws AccessDeniedException {
        if (Utilities.getUserDetails().getId() != ownerId && !Utilities.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        todoService.delete(todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") long userId, Model model) throws AccessDeniedException {
        if (Utilities.getUserDetails().getId() != userId && !Utilities.isAdmin()) {
            throw new AccessDeniedException("Access denied");
        }
        List<ToDo> todos = todoService.getByUserId(userId);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        model.addAttribute("userName", Utilities.getUserName());
        model.addAttribute("userId", Utilities.getUserDetails().getId());
        return "todos-user";
    }

    @GetMapping("/{id}/add")
    public String addCollaborator(@PathVariable long id, @RequestParam("user_id") long userId, Model model) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.add(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        model.addAttribute("userName", Utilities.getUserName());
        model.addAttribute("userId", Utilities.getUserDetails().getId());
        return "redirect:/todos/" + id + "/tasks";
    }

    @GetMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable long id, @RequestParam("user_id") long userId) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        return "redirect:/todos/" + id + "/tasks";
    }
}
