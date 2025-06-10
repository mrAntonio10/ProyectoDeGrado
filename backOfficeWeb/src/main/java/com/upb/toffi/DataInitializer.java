package com.upb.toffi;

import com.upb.models.operation.Operation;
import com.upb.models.permission.Permission;
import com.upb.models.resource.Resource;
import com.upb.models.rol.Rol;
import com.upb.models.rol_resource.RolResource;
import com.upb.models.rol_resource.dto.RolResourceDto;
import com.upb.models.user.User;
import com.upb.repositories.*;
import com.upb.toffi.config.util.PermissionsEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final ResourceRepository resourceRepository;
    private final RolResourceRepository rolResourceRepository;
    private final OperationRepository operationRepository;
    private final PermissionRepository permissionRepository;


    @Override
    public void run(String... args) throws Exception {
      this.createRolesAndUsers();
        this.createOperations();

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

        Optional<Rol> rolSalesPointOpt = rolRepository.findByNameAndStateTrue("SALES_POINT");
        Rol rolSalesPoint;
        if(!rolSalesPointOpt.isPresent()) {
            rolSalesPoint = Rol.builder()
                    .state(true)
                    .name("SALES_POINT")
                    .build();
            rolRepository.save(rolSalesPoint);
        } else {
            rolSalesPoint = rolSalesPointOpt.get();
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

        if (!userRepository.findByEmailAndStateActive("sales@gmail.com").isPresent()) {
            User salesUser = new User();
            salesUser.setName("SALES");
            salesUser.setLastname("Sales");
            salesUser.setEmail("sales@gmail.com");
            salesUser.setPassword(passwordEncoder.encode("sales"));
            salesUser.setState("ACTIVE");
            salesUser.setRol(rolSalesPoint);
            userRepository.save(salesUser);

            System.out.println("Usuario SALES creado.");
        } else {
            System.out.println("Usuario SALES ya existe.");
        }
    }

    private void createResources(){
        Rol root = rolRepository.findByNameAndStateTrue("ROOT").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario Root"));

        Rol admin = rolRepository.findByNameAndStateTrue("ADMIN").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario Admin"));

        Rol student = rolRepository.findByNameAndStateTrue("STUDENT").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario student"));

        Rol sales = rolRepository.findByNameAndStateTrue("SALES_POINT").orElseThrow(
                () -> new NoSuchElementException("No se encontró el rol de usuario sales_point"));

        //Recurso Padre - Gestión
        String idManagementResource = this.createUpdateResource("Gestión", "/dashboard/management", "pi pi-fw pi-database","Recurso padre para la gestión de empresas, sucursales y usuarios",null, 1, null, root, admin);
        this.createUpdateResource("Empresas", "/enterprise", "pi pi-fw pi-briefcase","Recurso encargado de gestionar las empresas dentro del sistema",idManagementResource, 1, PermissionsEnum.EnterprisePermissions.class, root);
        this.createUpdateResource("Sucursales", "/branchOffice", "pi pi-fw pi-building","Recurso encargado de gestionar las sucursales dentro del sistema",idManagementResource, 2, PermissionsEnum.BranchOfficePermissions.class, root, admin);
        this.createUpdateResource("Usuarios", "/user", "pi pi-fw pi-users","Recurso encargado de gestionar los usuarios dentro del sistema",idManagementResource, 3, PermissionsEnum.UserPermissions.class, root, admin);
        this.createUpdateResource("Almacén", "/warehouse", "pi pi-fw pi-book","Recurso encargado de gestionar los productos de un almacén dentro del sistema",idManagementResource, 4, PermissionsEnum.WarehousePermission.class, admin);


        //Recurso Padre - Ajustes
//        String idConfigurationResource = this.createUpdateResource("Ajustes", "/dashboard/configuration", "pi pi-fw pi-cog","Recurso padre para la gestión de dominios y parámetros del sistema",null, 2, null, root, admin);
//        this.createUpdateResource("Parámetros", "/parameter", "pi pi-fw pi-code","Recurso encargado de gestionar parámetros del sistema",idConfigurationResource, 1, null, root, admin);
//        this.createUpdateResource("Dominios", "/domain", "pi pi-fw pi-box","Recurso encargado de gestionar los dominios del sistema",idConfigurationResource, 2, null, root);
//        this.createUpdateResource("Permisos", "/permission", "pi pi-exclamation-triangle","Recurso encargado de gestionar los permisos por roles de usuarios en el sistema",idConfigurationResource, 3, null, root, admin);

        //Recurso Padre - Gestión comercial
        String idComercialManagementResource = this.createUpdateResource("Gestión comercial", "/dashboard/comercial-management", "pi pi-desktop","Recurso padre para la gestión comercial de puntos de ventas",null, 2, null, sales);
        this.createUpdateResource("Panel de venta", "/sales-panel", "pi pi-cart-plus","Recurso encargado de gestionar el panel de productos de un punto de venta",idComercialManagementResource, 1, PermissionsEnum.SalesPanelPermission.class, sales);
        this.createUpdateResource("Ventas", "/user-sales", "pi pi-dollar","Recurso encargado de gestionar las ventas realizadas por un usuario punto de venta",idComercialManagementResource, 2, PermissionsEnum.UserSalesPermission.class, sales);

//        this.createUpdateResource("Gestión de ventas", "/sales-management", "pi pi-dollar","Recurso encargado de gestionar las ventas realizadas por los usuarios punto de venta",idComercialManagementResource, 2, PermissionsEnum.UserSalesPermission.class, admin);
    }

    private void createOperations() {
        Optional<Operation> rOp = operationRepository.findOperationByName("REPORT");
        if(rOp.isEmpty()) {
            Operation reportOperation = Operation.builder()
                    .name("REPORT")
                    .description("Operación que permite otorgar permisos de generar reportes")
                    .state(true)
                    .build();
            operationRepository.save(reportOperation);
            log.info("Se creó la operación para generar reportes");
        }

        Optional<Operation> cOp = operationRepository.findOperationByName("CREATE");
        if(cOp.isEmpty()) {
            Operation createOperation = Operation.builder()
                    .name("CREATE")
                    .description("Operación que permite otorgar permisos de creación")
                    .state(true)
                    .build();
            operationRepository.save(createOperation);
            log.info("Se creó la operación para crear");
        }
        Optional<Operation> uOp = operationRepository.findOperationByName("UPDATE");
        if(uOp.isEmpty()) {
            Operation updateOperation = Operation.builder()
                    .name("UPDATE")
                    .description("Operación que permite otorgar permisos de actualización")
                    .state(true)
                    .build();
            operationRepository.save(updateOperation);
            log.info("Se creó la operación para actualizar");
        }
        Optional<Operation> dOp = operationRepository.findOperationByName("DELETE");
        if(dOp.isEmpty()) {
            Operation deleteOperation = Operation.builder()
                    .name("DELETE")
                    .description("Operación que permite otorgar permisos de eliminación")
                    .state(true)
                    .build();
            operationRepository.save(deleteOperation);
            log.info("Se creó la operación para eliminar");
        }
        Optional<Operation> vOp = operationRepository.findOperationByName("VIEW");
        if(vOp.isEmpty()) {
            Operation viewOperation = Operation.builder()
                    .name("VIEW")
                    .description("Operación que permite otorgar permisos de vista al recurso principal")
                    .state(true)
                    .build();
            operationRepository.save(viewOperation);
            log.info("Se creó la operación para visualizar");
        }
    }


    private String createUpdateResource(String resourceName, String url, String icon, String description, String parent, int priority, Class<? extends Enum<?>> permissionEnum, Rol... roles) {
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

            log.info("Se creó el recurso con nombre [{}]", resourceName);
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

                if(permissionEnum != null ){
                    //rol-recurso-permiso
                    for (Enum<?> enumValue : permissionEnum.getEnumConstants()) {
                        // Aquí puedes utilizar permissionValue como desees
                        Operation operation = operationRepository.findOperationByName(enumValue.toString())
                                .orElseThrow(() -> new NoSuchElementException("No se encontró el permiso " +enumValue.toString()));

                        Permission permission = Permission.builder()
                                .state(true)
                                .resource(resource)
                                .operation(operation)
                                .rol(rol)
                                .build();
                        log.info("Al rol [{}], y recurso [{}], se le adjunto la operacion de [{}]", rol.getName(), resource.getName(), operation.getName());

                        permissionRepository.save(permission);
                    }
                }
            }
        }

        return resource.getId();
    }
}
