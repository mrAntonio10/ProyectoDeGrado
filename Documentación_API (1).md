# Documentación de APIs para el Módulo de Usuarios

El Módulo de Usuarios abarca funcionalidades para la autenticación de usuarios, acceso a recursos, gestión de empresas, gestión de sucursales y gestión de usuarios dentro del sistema. A continuación, se detallan las especificaciones de las APIs para cada funcionalidad.

## 1. Funcionalidad de Inicio de Sesión

### API: Inicio de Sesión
**Propósito**: Autenticar a los usuarios mediante la validación de sus credenciales para iniciar sesión en el sistema.

| **Atributo**          | **Detalles**                                                                |
|-----------------------|-----------------------------------------------------------------------------|
| **URL**               | `api/v1/authenticate`                                                       |
| **Método**            | POST                                                                        |
| **Parámetros Body**   | - `correo/username`: String<br>- `password`: String                         |
| **Respuesta**         | JSON con `jwt`, `nombre_completo`, `rol`                                    |
| **Entidades**         | **Usuario**:<br>- `id`: String<br>- `name`: String (60)<br>- `lastname`: String (60)<br>- `password`: String (60)<br>- `email`: String (255)<br>- `phone_number`: String (20)<br>- `state`: Enum [activo, bloqueado, eliminado]<br>- `id_rol`: String<br>**Rol**:<br>- `id`: String<br>- `name`: String (60)<br>- `state`: Enum [disponible, eliminado]<br>**Dominio**:<br>- `id`: String<br>- `value`: String (30)<br>- `description`: String (120)<br>- `id_domain`: String |

## 2. Funcionalidad de Recursos del Menú

### API: Recursos del Menú
**Propósito**: Obtener los recursos asociados al rol del usuario autenticado para desplegar el menú de opciones correspondiente.

| **Atributo**          | **Detalles**                                                                |
|-----------------------|-----------------------------------------------------------------------------|
| **URL**               | `api/v1/resources`                                                          |
| **Método**            | GET                                                                         |
| **Encabezados**       | `Authority`                                                                 |
| **Respuesta**         | Lista ordenada de recursos (padres e hijos) asociados al rol                |
| **Entidades**         | **Recurso**:<br>- `id`: String<br>- `name`: String (150)<br>- `url`: String (255)<br>- `icon`: String (255)<br>- `state`: Booleano<br>- `parent_id`: String<br>- `priority`: Entero<br>- `description`: String (150)<br>**Rol_Recurso**:<br>- `id`: String<br>- `id_rol`: String<br>- `id_resource`: String |

## 3. Funcionalidad de Empresas

### API: Empresas
**Propósito**: Gestionar la visualización paginada, creación, eliminación y modificación de empresas habilitadas en el sistema, con acciones basadas en los permisos del usuario autenticado.

| **Atributo**          | **Detalles**                                                                |
|-----------------------|-----------------------------------------------------------------------------|
| **URL**               | `api/v1/enterprises`                                                        |
| **Métodos**           | **GET**:<br>- Respuesta: Lista paginada de empresas `{id, nombre, email, número_teléfono}`<br>**POST**:<br>- Body: `nombre`, `logo` (opcional), `descripción` (opcional), `número_teléfono`, `email`, `estado: ACTIVO`<br>- Respuesta: `{id, nombre}` de la empresa creada<br>**DELETE**:<br>- Parámetro: `/{id-enterprise}`<br>- Respuesta: `{id, nombre}` de la empresa eliminada/bloqueada<br>**PUT**:<br>- Body: `id`, `nombre`, `logo` (opcional), `descripción` (opcional), `número_teléfono`, `email`, `estado`<br>- Respuesta: `{id, nombre, email, número_teléfono}`<br>**GET (por ID)**:<br>- Parámetro: `/by-id/{id_enterprise}`<br>- Respuesta: DTO con todos los atributos de la empresa solicitada |
| **Entidades**         | **Empresa**:<br>- `id`: String<br>- `nombre`: String (120)<br>- `logo`: String (255, nulo)<br>- `descripción`: String (150, nulo)<br>- `número_teléfono`: String (20)<br>- `email`: String (255)<br>- `estado`: Enum [activo, bloqueado, eliminado]<br>**Operación**:<br>- `id`: String<br>- `nombre`: String (60)<br>- `descripción`: String (150)<br>- `estado`: Booleano<br>**Permiso**:<br>- `id`: String<br>- `id_operación`: String<br>- `id_recurso`: String<br>- `estado`: Booleano<br>- `id_rol`: String |

## 4. Funcionalidad de Sucursales

### API: Sucursales
**Propósito**: Gestionar la visualización paginada, creación, eliminación y modificación de sucursales pertenecientes a una empresa habilitada, con operaciones habilitadas según los permisos del rol del usuario autenticado.

| **Atributo**          | **Detalles**                                                                |
|-----------------------|-----------------------------------------------------------------------------|
| **URL**               | `api/v1/branch-offices`                                                     |
| **Métodos**           | **GET**:<br>- Parámetro: `/{id_enterprise}`<br>- Respuesta: Lista paginada de sucursales `{id, nombre, ubicación, factura, estado}`<br>**POST**:<br>- Body: `id`, `nombre`, `ubicación`, `IN_CODE` (opcional, requerido si factura), `número_teléfono` (opcional), `id_empresa`, `estado: ACTIVO`, `factura`<br>- Respuesta: `{id, nombre}` de la sucursal creada<br>**PUT (Cambiar Estado)**:<br>- Parámetro: `/change-state/{id_enterprise}/{estado}`<br>- Respuesta: `{id, nombre}` de la sucursal eliminada/bloqueada<br>**UPDATE**:<br>- Parámetro: `/{id_enterprise}`<br>- Body: `id`, `nombre`, `ubicación`, `IN_CODE` (opcional, requerido si factura), `número_teléfono` (opcional), `id_empresa`, `estado`, `factura`<br>- Respuesta: `{id, nombre}` de la sucursal actualizada<br>**GET (por ID)**:<br>- Parámetro: `/by-id/{id_branchOffice}`<br>- Respuesta: DTO con todos los atributos de la sucursal solicitada |
| **Entidades**         | **Sucursal**:<br>- `id`: String<br>- `nombre`: String (120)<br>- `ubicación`: String (255)<br>- `IN_CODE`: String (255, nulo)<br>- `número_teléfono`: String (20, nulo)<br>- `id_empresa`: String<br>- `estado`: Enum [activo, bloqueado, eliminado]<br>- `factura`: Booleano (Facturan: Sí/No) |

## 5. Funcionalidad de Usuarios

### API: Usuarios
**Propósito**: Gestionar la visualización paginada, creación, eliminación y modificación de usuarios asociados a una sucursal habilitada, con operaciones habilitadas según los permisos del rol del usuario autenticado.

| **Atributo**          | **Detalles**                                                                |
|-----------------------|-----------------------------------------------------------------------------|
| **URL**               | `api/v1/users`                                                              |
| **Métodos**           | **GET**:<br>- Respuesta: Lista paginada de usuarios<br>**POST**:<br>- Body: `nombre`, `apellido`, `contraseña`, `email`, `número_teléfono`, `estado: ACTIVO`, `id_rol`, `id_usuario_sucursal` (vinculado al servicio usuario_sucursal, mismo estado que el usuario)<br>- Respuesta: `{id, nombre_completo}` del usuario creado<br>** Fondo de pantalla brillante**UPDATE**:<br>- Parámetro: `/{id_user}`<br>- Body: `nombre`, `apellido`, `contraseña`, `email`, `número_teléfono`, `estado`, `id_rol`, `id_usuario_sucursal` (vinculado al servicio usuario_sucursal, mismo estado que el usuario)<br>- Respuesta: `{id, nombre}` del usuario actualizado<br>**POST (Cambiar Estado)**:<br>- Parámetro: `/{id_user}/{estado}`<br>- Respuesta: `{id, nombre}` del usuario eliminado/bloqueado (vinculado al servicio usuario_sucursal, mismo estado que el usuario)<br>**GET (por ID)**:<br>- Parámetro: `/by-id/{id_user}`<br>- Respuesta: DTO con todos los atributos del usuario solicitado |
| **Entidades**         | **Usuario_Sucursal**:<br>- `id`: String<br>- `id_usuario`: String<br>- `id_sucursal`: String<br>- `estado`: Enum [activo, bloqueado, eliminado] |