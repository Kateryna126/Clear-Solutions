package clearsolutions.testassignment_java.controller;

import clearsolutions.testassignment_java.model.User;
import clearsolutions.testassignment_java.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setBirthDate(Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

    }

    @Test
    void testCreateUserUnderage() throws Exception {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setBirthDate(Date.from(LocalDate.now().minusYears(17).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException("User must be at least 18 years old"));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testUpdateUserFields() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");

        User updates = new User();
        updates.setEmail("existing@example.com");
        updates.setFirstName("Updated");
        updates.setLastName("User");

        when(userService.updateUserFields(any(String.class), any(User.class))).thenReturn(updates);

        mockMvc.perform(patch("/user/existing@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("existing@example.com")));
    }


    @Test
    void testUpdateUser() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");

        User updatedUser = new User();
        updatedUser.setEmail("existing@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");

        when(userService.updateUser(any(String.class), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/user/existing@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.lastName", is("User")))
                .andExpect(jsonPath("$.email", is("existing@example.com")));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(any(String.class));

        mockMvc.perform(delete("/user/existing@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser("existing@example.com");
    }

    @Test
    void testSearchUsers() throws Exception {

        List<User> users = new ArrayList<>();
        User user = new User();
        user.setEmail("existing@example.com");
        user.setFirstName("Existing");
        user.setLastName("User");
        users.add(user);


        when(userService.searchUsers(any(Date.class), any(Date.class))).thenReturn(users);


        mockMvc.perform(MockMvcRequestBuilders.get("/user/search")
                        .param("from", "2022-01-01")
                        .param("to", "2022-12-31")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("existing@example.com")));
    }

    @Test
    void testHandleIllegalArgumentException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/user")
                        .content("{from\": \"2024-01-01\", \"to\": \"2023-01-01}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("invalid-email");
        user.setBirthDate(Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException("Invalid email format"));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserFieldsWithNonExistingUser() throws Exception {
        User updates = new User();
        updates.setEmail("non-existing@example.com");
        updates.setFirstName("Updated");
        updates.setLastName("User");

        when(userService.updateUserFields(any(String.class), any(User.class))).thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(patch("/user/non-existing@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updates)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUserWithNonExistingUser() throws Exception {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(any(String.class));

        mockMvc.perform(delete("/user/non-existing@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSearchUsersWithInvalidDates() throws Exception {
        Date from = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(userService.searchUsers(any(Date.class), any(Date.class))).thenThrow(new IllegalArgumentException("'From' date must be before 'To' date"));

        mockMvc.perform(get("/user/search")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
