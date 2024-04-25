package clearsolutions.testassignment_java.controller;

import clearsolutions.testassignment_java.model.User;
import clearsolutions.testassignment_java.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
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

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam Date from, @RequestParam Date to) {
        if (from.after(to)) {
            throw new IllegalArgumentException("'From' date must be before 'To' date");
        }
        return userService.searchUsers(from, to);
    }
}
