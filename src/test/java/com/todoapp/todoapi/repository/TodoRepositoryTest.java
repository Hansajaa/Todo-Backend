package com.todoapp.todoapi.repository;

import com.todoapp.todoapi.entity.TodoEntity;
import com.todoapp.todoapi.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void itShouldReturnTodoEntitiesWithPagination() {
        // given
        Pageable pageable = PageRequest.of(0, 5); // First page, 5 items per page

        // Insert test data
        UserEntity user = UserEntity.builder().email("sample@gamil.com").password("123").build();
        UserEntity savedUser = userRepository.save(user);
        TodoEntity todo1 = TodoEntity.builder().title("title1").body("apple").Status("Pending").date(new Date()).priority("Medium").user(savedUser).build();
        TodoEntity todo2 = TodoEntity.builder().title("title2").body("banana").Status("Pending").date(new Date()).priority("Medium").user(savedUser).build();
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        // Act
        Page<TodoEntity> result = todoRepository.findAllByUserIdWithPagination(savedUser.getId(), pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title1");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("title2");
    }

    @Test
    void itShouldReturnSearchTodosByKeywords() {
        // Arrange: Set up a user and todos
        UserEntity user = UserEntity.builder().email("testuser@example.com").password("password").build();
        user = userRepository.save(user);  // Save and get managed user with ID

        TodoEntity todo1 = TodoEntity.builder().title("Shopping").body("Buy groceries").Status("Pending")
                .date(new Date()).priority("High").user(user).build();
        TodoEntity todo2 = TodoEntity.builder().title("Workout").body("Morning exercise").Status("Completed")
                .date(new Date()).priority("Medium").user(user).build();
        TodoEntity todo3 = TodoEntity.builder().title("Project").body("Finish project report").Status("Progress")
                .date(new Date()).priority("Low").user(user).build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        // Act & Assert: Test with various criteria
        // Test case 1: Search by title "Shopping"
        List<TodoEntity> result = todoRepository.searchTodos(user.getId(), "Shopping", null, null, null);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Shopping");

        // Test case 2: Search by status "Completed"
        result = todoRepository.searchTodos(user.getId(), null, null, "Completed", null);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Workout");

        // Test case 3: Search by priority "Low"
        result = todoRepository.searchTodos(user.getId(), null, null, null, "Low");
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Project");

        // Test case 4: Search by title containing "Proj" (partial match)
        result = todoRepository.searchTodos(user.getId(), "Proj", null, null, null);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Project");

        // Test case 5: Search by multiple criteria - title "Workout" and status "Completed"
        result = todoRepository.searchTodos(user.getId(), "Workout", null, "Completed", null);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Workout");

        // Test case 6: Search with all filters as null - should return all user's todos
        result = todoRepository.searchTodos(user.getId(), null, null, null, null);
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void itShouldReturnTodoEntitiesByUserIdAndCompletionStatus() {
        // Arrange: Set up a user and todos with different completion statuses
        UserEntity user = UserEntity.builder().email("testuser@example.com").password("password").build();
        user = userRepository.save(user);  // Save user to get an ID

        TodoEntity todo1 = TodoEntity.builder().title("Task 1").body("Body 1").Status("Pending")
                .date(new Date()).priority("High").completionStatus(false).user(user).build();
        TodoEntity todo2 = TodoEntity.builder().title("Task 2").body("Body 2").Status("Completed")
                .date(new Date()).priority("Medium").completionStatus(true).user(user).build();
        TodoEntity todo3 = TodoEntity.builder().title("Task 3").body("Body 3").Status("Pending")
                .date(new Date()).priority("Low").completionStatus(false).user(user).build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        // Act & Assert: Test for completionStatus = true
        List<TodoEntity> completedTodos = todoRepository.findByUserIdAndCompletionStatus(user.getId(), true);
        assertThat(completedTodos.size()).isEqualTo(1);
        assertThat(completedTodos.get(0).getTitle()).isEqualTo("Task 2");

        // Act & Assert: Test for completionStatus = false
        List<TodoEntity> incompleteTodos = todoRepository.findByUserIdAndCompletionStatus(user.getId(), false);
        assertThat(incompleteTodos.size()).isEqualTo(2);
        // Explicitly check the titles of the returned entities without using extracting
        boolean containsTask1 = incompleteTodos.stream().anyMatch(todo -> todo.getTitle().equals("Task 1"));
        boolean containsTask3 = incompleteTodos.stream().anyMatch(todo -> todo.getTitle().equals("Task 3"));

        assertThat(containsTask1).isTrue();
        assertThat(containsTask3).isTrue();
    }
}