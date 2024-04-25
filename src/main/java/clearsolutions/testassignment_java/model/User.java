package clearsolutions.testassignment_java.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
public class User {
    @Email
    @NotNull
    private String email;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @Past
    @NotNull
    private Date birthDate;
    private String address;
    private String phoneNumber;
}
