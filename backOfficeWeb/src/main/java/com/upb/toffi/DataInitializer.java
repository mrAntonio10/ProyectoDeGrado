package com.upb.toffi;

import com.upb.models.rol.Rol;
import com.upb.models.user.User;
import com.upb.repositories.RolRepository;
import com.upb.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;


    @Override
    public void run(String... args) throws Exception {

        //Roles
        Rol rolRoot = Rol.builder()
                .state(true)
                .name("ROOT")
                .build();
        rolRepository.save(rolRoot);

        Rol rolStudent = Rol.builder()
                .state(true)
                .name("STUDENT")
                .build();
        rolRepository.save(rolStudent);

        Rol rolAdmin = Rol.builder()
                .state(true)
                .name("admin")
                .build();
        rolRepository.save(rolAdmin);

        if (!userRepository.findByEmailAndStateActive("admin@gmail.com").isPresent()) {
            User adminUser = new User();
            adminUser.setName("ADMIN");
            adminUser.setLastname("Administrator");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin"));  // Codifica la contraseña
            adminUser.setState("ACTIVE");
            adminUser.setRol(rolAdmin);
            userRepository.save(adminUser);

            System.out.println("Usuario ADMIN creado.");
        } else {
            System.out.println("Usuario ADMIN ya existe.");
        }

        if (!userRepository.findByEmailAndStateActive("root@gmail.com").isPresent()) {
            User rootUser = new User();
            rootUser.setName("ROOT");
            rootUser.setLastname("Root");
            rootUser.setEmail("root@gmail.com");
            rootUser.setPassword(passwordEncoder.encode("root"));  // Codifica la contraseña
            rootUser.setState("ACTIVE");
            rootUser.setRol(rolRoot);
            userRepository.save(rootUser);

            System.out.println("Usuario ROOT creado.");
        } else {
            System.out.println("Usuario ROOT ya existe.");
        }

        if (!userRepository.findByEmailAndStateActive("student@gmail.com").isPresent()) {
            User studentUser = new User();
            studentUser.setName("STUDENT");
            studentUser.setLastname("Student");
            studentUser.setEmail("student@gmail.com");
            studentUser.setPassword(passwordEncoder.encode("student"));  // Codifica la contraseña
            studentUser.setState("ACTIVE");
            studentUser.setRol(rolStudent);
            userRepository.save(studentUser);

            System.out.println("Usuario STUDENT creado.");
        } else {
            System.out.println("Usuario STUDENT ya existe.");
        }
    }
}
