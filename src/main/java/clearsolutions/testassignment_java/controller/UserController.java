package clearsolutions.testassignment_java.controller;

import clearsolutions.testassignment_java.model.User;
import clearsolutions.testassignment_java.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping("/{email}")
    public User updateUserFields(@PathVariable String email, @RequestBody User userUpdates) {
        return userService.updateUserFields(email, userUpdates);
    }

    @PutMapping("/{email}")
    public User updateUser(@PathVariable String email, @RequestBody User updatedUser) {
        return userService.updateUser(email, updatedUser);
    }

    @DeleteMapping("/{email}")
    public void deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
    }

    @GetMapping("/search") // Mapping for the search endpoint
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to) {
        List<User> users = userService.searchUsers(from, to);
        return ResponseEntity.ok(users);
    }
}
