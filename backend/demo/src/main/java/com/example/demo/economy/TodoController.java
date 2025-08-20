package com.example.demo.economy;

import com.example.demo.economy.request.CreateEpicRequest;
import com.example.demo.economy.request.CreateTaskRequest;
import com.example.demo.economy.request.SortTaskRequest;
import com.example.demo.economy.request.UpdateEpicRequest;
import com.example.demo.economy.request.UpdateTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/economy")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/epic")
    public ResponseEntity<Object> findEpic() {

        return ResponseEntity.ok().body(todoService.findEpic());
    }

    @PostMapping("/epic")
    public ResponseEntity<Object> createEpic(@RequestBody CreateEpicRequest request) {

        return ResponseEntity.ok().body(todoService.createEpic(request));
    }

    @PutMapping("/epic")
    public ResponseEntity<Object> updateEpic(@RequestBody UpdateEpicRequest request) {

        todoService.updateEpic(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/epic/{epicId}")
    public ResponseEntity<Object> deleteEpic(@PathVariable("epicId") Long epicId) {

        todoService.deleteEpic(epicId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/task")
    public ResponseEntity<Object> createTask(@RequestBody CreateTaskRequest request) {

        return ResponseEntity.ok().body(todoService.createTask(request));
    }

    @PutMapping("/task")
    public ResponseEntity<Object> updateTask(@RequestBody UpdateTaskRequest request) {

        todoService.updateTask(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Object> deleteTask(@PathVariable("taskId") Long taskId) {

        todoService.deleteTask(taskId);
        return ResponseEntity.ok().build();
    }

    // task 정렬
    @PostMapping("/task/sort")
    public ResponseEntity<Object> sortTask(@RequestBody SortTaskRequest request) {

        todoService.sortTask(request);
        return ResponseEntity.ok().build();
    }
}
