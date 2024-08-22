package com.upb.toffi;

import com.upb.models.resource.Resource;
import com.upb.models.rol.Rol;
import com.upb.models.rol_resource.RolResource;
import com.upb.models.rol_resource.dto.RolResourceDto;
import com.upb.models.user.User;
import com.upb.repositories.ResourceRepository;
import com.upb.repositories.RolRepository;
import com.upb.repositories.RolResourceRepository;
import com.upb.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final ResourceRepository resourceRepository;
    private final RolResourceRepository rolResourceRepository;


    @Override
    public void run(String... args) throws Exception {
      this.createRolesAndUsers();
      this.createResources();
    }


    private void createRolesAndUsers() {

        //Roles
        Optional<Rol> rolRootOpt = rolRepository.findByNameAndStateTrue("ROOT");
        Rol rolRoot;
        if(!rolRootOpt.isPresent()) {
            rolRoot = Rol.builder()
                    .state(true)
                    .name("ROOT")
                    .build();
            rolRepository.save(rolRoot);
        } else {
            rolRoot = rolRootOpt.get();
        }

        Optional<Rol> rolStudentOpt = rolRepository.findByNameAndStateTrue("STUDENT");
        Rol rolStudent;
        if(!rolStudentOpt.isPresent()) {
             rolStudent = Rol.builder()
                    .state(true)
                    .name("STUDENT")
                    .build();
            rolRepository.save(rolStudent);
        } else {
            rolStudent = rolStudentOpt.get();
        }

        Optional<Rol> rolAdminOpt = rolRepository.findByNameAndStateTrue("ADMIN");
        Rol rolAdmin;
        if(!rolAdminOpt.isPresent()) {
             rolAdmin = Rol.builder()
                    .state(true)
                    .name("ADMIN")
                    .build();
            rolRepository.save(rolAdmin);
        } else {
            rolAdmin = rolAdminOpt.get();
        }

        if (!userRepository.findByEmailAndStateActive("admin@gmail.com").isPresent()) {
            User adminUser = new User();
            adminUser.setName("ADMIN");
            adminUser.setLastname("Administrator");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin"));
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
            rootUser.setPassword(passwordEncoder.encode("root"));
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
            studentUser.setPassword(passwordEncoder.encode("student"));
            studentUser.setState("ACTIVE");
            studentUser.setRol(rolStudent);
            userRepository.save(studentUser);

            System.out.println("Usuario STUDENT creado.");
        } else {
            System.out.println("Usuario STUDENT ya existe.");
        }
    }

    private void createResources(){
        Rol root = rolRepository.findByNameAndStateTrue("ROOT").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario Root"));

        Rol admin = rolRepository.findByNameAndStateTrue("ADMIN").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario Admin"));

        Rol student = rolRepository.findByNameAndStateTrue("STUDENT").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario student"));

        //Recurso Padre - Gestión
        String idManagementResource = this.createUpdateResource("Gestión", "/management", "pi pi-fw pi-database","Recurso padre para la gestión de empresas, sucursales y usuarios",null, 1, root, admin);
        this.createUpdateResource("Empresas", "/enterprise", "pi pi-fw pi-briefcase","Recurso encargado de gestionar las empresas dentro del sistema",idManagementResource, 9, root);
        this.createUpdateResource("Sucursales", "/branchOficce", "pi pi-fw pi-building","Recurso encargado de gestionar las sucursales dentro del sistema",idManagementResource, 2, root, admin);
        //Recurso Padre - Ajustes
        String idConfigurationResource = this.createUpdateResource("Ajustes", "/configuration", "pi pi-fw pi-cog","Recurso padre para la gestión de dominios y parámetros del sistema",null, 2, root, admin);
        this.createUpdateResource("Parámetros", "/parameter", "pi pi-fw pi-code","Recurso encargado de gestionar parámetros del sistema",idConfigurationResource, 20, root, admin);
        this.createUpdateResource("Dominios", "/domain", "pi pi-fw pi-box","Recurso encargado de gestionar los dominios del sistema",idConfigurationResource, 9, root);

    }


    private String createUpdateResource(String resourceName, String url, String icon, String description, String parent, int priority, Rol... roles) {
        Optional<Resource> resourceOpt = resourceRepository.findByNameAndStateTrue(resourceName);
        Resource resource;
        if(resourceOpt.isPresent()) {
            resource = resourceOpt.get();

            resource.setUrl(url);
            resource.setIcon(icon);
            resource.setDescription(description);
            resource.setIdParent(parent);
            resource.setPriority(priority);
            resource.setState(true);
        } else {
            resource = Resource.builder()
                    .name(resourceName)
                    .url(url)
                    .icon(icon)
                    .idParent(parent)
                    .description(description)
                    .priority(priority)
                    .state(true)
                    .build();
        }

        resourceRepository.save(resource);

        List<RolResourceDto> rrDtoList = rolResourceRepository.findByResourceNameAndResourceStateTrue(resourceName);

        if(rrDtoList.isEmpty()) {
            for (Rol rol: roles) {
                RolResource rolResource = RolResource.builder()
                        .rol(rol)
                        .resource(resource)
                        .build();

                rolResourceRepository.save(rolResource);
            }
        }

        return resource.getId();
    }
}
