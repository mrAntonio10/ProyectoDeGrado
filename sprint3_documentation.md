# Sprint 3: Almacén y Productos

## Objetivo
El propósito de este sprint es gestionar el almacén de una sucursal y los productos registrados en él, proporcionando APIs para administrar productos y el inventario del almacén.

## API de Productos
La API de productos expone endpoints para crear, actualizar, eliminar, listar y buscar productos por ID.

**Base URL**: `api/v1/products`

### Endpoints

| Método | Endpoint                     | Descripción                              |
|--------|------------------------------|------------------------------------------|
| POST   | `/`                          | Crea un nuevo producto.                  |
| PUT    | `/`                          | Actualiza un producto existente.         |
| DELETE | `/{id-product}`              | Elimina un producto por ID.              |
| GET    | `/list/{category}`           | Lista productos por categoría.           |
| GET    | `/{id-product}`              | Obtiene un producto por ID.              |

#### POST: Crear Producto
- **URL**: `api/v1/products`
- **Parámetros (Body)**:

| Campo            | Tipo   | Descripción                                      | Requerido |
|------------------|--------|--------------------------------------------------|-----------|
| name             | String | Nombre del producto (máx. 60 caracteres).        | Sí        |
| category         | String | Categoría: bebidas, almuerzos, sándwiches, horneados, empanadas. | Sí        |
| beverageFormat   | String | Formato (solo si es bebida): personal, vaso grande, vaso chico, 1L, 2L. | No        |

- **Respuesta**:

| Campo    | Tipo   | Descripción                     |
|----------|--------|---------------------------------|
| id       | String | Identificador del producto.     |
| name     | String | Nombre del producto.           |
| category | String | Categoría del producto.        |

#### PUT: Actualizar Producto
- **URL**: `api/v1/products`
- **Parámetros (Body)**:

| Campo            | Tipo   | Descripción                                      | Requerido |
|------------------|--------|--------------------------------------------------|-----------|
| id               | String | Identificador del producto.                      | Sí        |
| name             | String | Nombre del producto (máx. 60 caracteres).        | Sí        |
| category         | String | Categoría: bebidas, almuerzos, sándwiches, horneados, empanadas. | Sí        |
| beverageFormat   | String | Formato (solo si es bebida): personal, vaso grande, vaso chico, 1L, 2L. | No        |

- **Respuesta**:

| Campo    | Tipo   | Descripción                     |
|----------|--------|---------------------------------|
| id       | String | Identificador del producto.     |
| name     | String | Nombre del producto.           |
| category | String | Categoría del producto.        |

#### DELETE: Eliminar Producto
- **URL**: `api/v1/products/{id-product}`
- **Parámetros**: ID del producto en la URL.
- **Respuesta**:

| Campo    | Tipo   | Descripción                     |
|----------|--------|---------------------------------|
| id       | String | Identificador del producto.     |
| name     | String | Nombre del producto.           |
| state    | String | Estado del producto.           |

#### GET: Listar Productos por Categoría
- **URL**: `api/v1/products/list/{category}`
- **Parámetros**: Categoría en la URL.
- **Respuesta**:

| Campo    | Tipo   | Descripción                     |
|----------|--------|---------------------------------|
| id       | String | Identificador del producto.     |
| name     | String | Nombre del producto.           |

#### GET: Obtener Producto por ID
- **URL**: `api/v1/products/{id-product}`
- **Parámetros**: ID del producto en la URL.
- **Respuesta**:

| Campo    | Tipo   | Descripción                     |
|----------|--------|---------------------------------|
| id       | String | Identificador del producto.     |
| name     | String | Nombre del producto.           |

### Entidad: Productos
| Campo            | Tipo   | Descripción                                      | Restricciones             |
|------------------|--------|--------------------------------------------------|---------------------------|
| id               | String | Identificador único del producto.                |                           |
| name             | String | Nombre del producto.                             | Máx. 60 caracteres        |
| category         | String | Categoría del producto.                          | Máx. 30 caracteres        |
| beverageFormat   | String | Formato de bebida (si aplica).                   | Máx. 30 caracteres        |

## API de Almacén
La API de almacén administra el inventario de una sucursal, con acciones basadas en los permisos del usuario autenticado.

**Base URL**: `api/v1/warehouses`

### Endpoints

| Método | Endpoint                     | Descripción                              |
|--------|------------------------------|------------------------------------------|
| GET    | `/`                          | Lista productos en almacén con filtros.  |
| GET    | `/{id-warehouse}`            | Obtiene un registro de almacén por ID.   |
| POST   | `/`                          | Crea un nuevo registro en almacén.       |
| PUT    | `/`                          | Actualiza un registro en almacén.        |
| DELETE | `/{id-warehouse}`            | Elimina un registro de almacén por ID.   |

#### GET: Listar Productos en Almacén
- **URL**: `api/v1/warehouses`
- **Filtros (Query Parameters)**:

| Filtro          | Tipo    | Descripción                     |
|-----------------|---------|---------------------------------|
| productName     | String  | Nombre del producto.            |
| productCategory | String  | Categoría del producto.         |
| optimal_level   | Boolean | Indica nivel óptimo de stock.   |

- **Respuesta (Lista Paginada)**:

| Campo            | Tipo        | Descripción                     |
|------------------|-------------|---------------------------------|
| id               | String      | Identificador del registro.     |
| productName      | String      | Nombre del producto.           |
| productCategory  | String      | Categoría del producto.        |
| unitary_price    | BigDecimal  | Precio unitario.               |
| optimal_level    | Boolean     | Indica si el stock es óptimo.  |
| max              | BigInteger  | Máximo stock permitido.        |
| min              | BigInteger  | Mínimo stock requerido.        |

#### GET: Obtener Registro de Almacén por ID
- **URL**: `api/v1/warehouses/{id-warehouse}`
- **Parámetros**: ID del registro en la URL.
- **Respuesta**:

| Campo            | Tipo        | Descripción                     |
|------------------|-------------|---------------------------------|
| id               | String      | Identificador del registro.     |
| productName      | String      | Nombre del producto.           |
| productCategory  | String      | Categoría del producto.        |
| unitary_price    | BigDecimal  | Precio unitario.               |
| max              | BigInteger  | Máximo stock permitido.        |
| min              | BigInteger  | Mínimo stock requerido.        |

#### POST: Crear Registro en Almacén
- **URL**: `api/v1/warehouses`
- **Parámetros (Body)**:

| Campo            | Tipo        | Descripción                        | Requerido |
|------------------|-------------|------------------------------------|-----------|
| idProduct        | String      | ID del producto.                   | Sí        |
| idBranchOffice   | String      | ID de la sucursal.                 | Sí        |
| stock            | BigInteger  | Cantidad en stock.                 | Sí        |
| unitaryPrice     | BigDecimal  | Precio unitario.                   | Sí        |
| maxProduct       | BigInteger  | Máximo stock permitido.            | Sí        |
| minProduct       | BigInteger  | Mínimo stock requerido.            | Sí        |

- **Respuesta**:

| Campo            | Tipo        | Descripción                     |
|------------------|-------------|---------------------------------|
| id               | String      | Identificador del registro.     |
| productName      | String      | Nombre del producto.           |
| stock            | BigInteger  | Cantidad en stock.             |
| unitaryPrice     | BigDecimal  | Precio unitario.               |

#### PUT: Actualizar Registro en Almacén
- **URL**: `api/v1/warehouses`
- **Parámetros (Body)**:

| Campo            | Tipo        | Descripción                        | Requerido |
|------------------|-------------|------------------------------------|-----------|
| id               | String      | ID del registro.                   | Sí        |
| idProduct        | String      | ID del producto.                   | Sí        |
| idBranchOffice   | String      | ID de la sucursal.                 | Sí        |
| stock            | BigInteger  | Cantidad en stock.                 | Sí        |
| unitaryPrice     | BigDecimal  | Precio unitario.                   | Sí        |
| maxProduct       | BigInteger  | Máximo stock permitido.            | Sí        |
| minProduct       | BigInteger  | Mínimo stock requerido.            | Sí        |

- **Respuesta**:

| Campo            | Tipo        | Descripción                     |
|------------------|-------------|---------------------------------|
| id               | String      | Identificador del registro.     |
| productName      | String      | Nombre del producto.           |
| stock            | BigInteger  | Cantidad en stock.             |
| unitaryPrice     | BigDecimal  | Precio unitario.               |

#### DELETE: Eliminar Registro de Almacén
- **URL**: `api/v1/warehouses/{id-warehouse}`
- **Parámetros**: ID del registro en la URL.
- **Respuesta**: Sin contenido (204 No Content).

### Entidad: Almacén
| Campo            | Tipo        | Descripción                        | Restricciones             |
|------------------|-------------|------------------------------------|---------------------------|
| id               | String      | Identificador único del registro.  |                           |
| idProduct        | String      | ID del producto.                   | No nulo                   |
| stock            | BigInteger  | Cantidad en stock.                 |                           |
| unitary_price    | BigDecimal  | Precio unitario.                   |                           |
| idBranchOffice   | String      | ID de la sucursal.                 | No nulo                   |
| state            | String      | Estado del registro.               | Máx. 20 caracteres, no nulo |
| maxProduct       | BigInteger  | Máximo stock permitido.            |                           |
| minProduct       | BigInteger  | Mínimo stock requerido.            |                           |
