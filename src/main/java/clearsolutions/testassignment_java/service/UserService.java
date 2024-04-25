package clearsolutions.testassignment_java.service;

import clearsolutions.testassignment_java.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Value("${user.min.age}")
    private int minAge;

    private List<User> users = new ArrayList<>();

    public User createUser(User user) {
        LocalDate birthDate = user.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (Period.between(birthDate, LocalDate.now()).getYears() < minAge) {
            throw new IllegalArgumentException("User must be at least " + minAge + " years old");
        }
        users.add(user);
        return user;
    }

    public User updateUserFields(String email, User userUpdates) {
        User user = users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userUpdates.getFirstName() != null) {
            user.setFirstName(userUpdates.getFirstName());
        }
        return user;
    }

    public User updateUser(String email, User updatedUser) {
        User user = users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());

        return user;
    }

    public void deleteUser(String email) {
        users.removeIf(u -> u.getEmail().equals(email));
    }

    public List<User> searchUsers(Date from, Date to) {
        return users.stream()
                .filter(user -> user.getBirthDate().after(from) && user.getBirthDate().before(to))
                .collect(Collectors.toList());
    }
}
