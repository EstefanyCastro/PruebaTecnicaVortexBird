# Movie Ticket Booking System - Backend

## Description
Backend API para el Sistema de Compra de Entradas de Películas desarrollado con Spring Boot 3.2.0 y Java 17.

## Prerequisites
- Java 17 or higher
- Maven 3.8+
- PostgreSQL 12+
- IDE: IntelliJ IDEA o Eclipse

## Project Structure
```
src/main/java/com/vortexbird/movieticket/
├── presentation/          # REST Controllers
├── application/           # DTOs y Service Interfaces
├── domain/                # Entidades JPA
├── infrastructure/        # Repositories y Configuración
└── shared/                # Excepciones y Respuestas comunes
```

## Layer Architecture

### 1. Presentation Layer
Controladores REST que exponen los endpoints de la API.

**Controllers:**
- `MovieController`: Gestión de películas (CRUD)
- `CustomerController`: Gestión de clientes y autenticación
- `TicketPurchaseController`: Gestión de compras de entradas

### 2. Application Layer
Contiene la lógica de negocio y DTOs para validación.

**Services:**
- `IMovieService`: Operaciones de películas
- `ICustomerService`: Operaciones de clientes
- `ITicketPurchaseService`: Operaciones de compras

**DTOs:**
- `CreateMovieDTO`
- `MovieDTO`
- `RegisterCustomerDTO`
- `CustomerDTO`
- `CreateTicketPurchaseDTO`
- `TicketPurchaseDTO`

### 3. Domain Layer
Modelos de dominio que representan las entidades del negocio.

**Entities:**
- `Movie`: Películas disponibles
- `Customer`: Clientes registrados
- `TicketPurchase`: Compras realizadas

### 4. Infrastructure Layer
Acceso a datos y configuraciones.

**Repositories:**
- `MovieRepository`: Operaciones CRUD para películas
- `CustomerRepository`: Operaciones CRUD para clientes
- `TicketPurchaseRepository`: Operaciones CRUD para compras

**Configurations:**
- `CorsConfig`: Configuración CORS
- `SecurityConfig`: Configuración de seguridad y encriptación

### 5. Shared Layer
Utilidades compartidas.

**Exceptions:**
- `ResourceNotFoundException`: Recurso no encontrado
- `BusinessException`: Violación de regla de negocio

**Response:**
- `ApiResponse<T>`: Respuesta estandarizada de API

## Database Setup

```sql
-- Crear base de datos
CREATE DATABASE movie_ticket_db;

-- Las tablas se crean automáticamente con Hibernate DDL
-- Asegúrate de configurar: spring.jpa.hibernate.ddl-auto=create
```

## Configuration

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/movie_ticket_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
      
jwt:
  secret: your-secret-key-here
  expiration: 86400000
  
aws:
  s3:
    bucket: movie-tickets-bucket
    region: us-east-1
```

## Building and Running

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

## API Endpoints

### Movies
```
GET    /api/v1/movies           - Obtener todas las películas
GET    /api/v1/movies/:id       - Obtener película por ID
POST   /api/v1/movies           - Crear película
PUT    /api/v1/movies/:id       - Actualizar película
DELETE /api/v1/movies/:id       - Desactivar película
```

### Customers
```
POST   /api/v1/customers/register  - Registrar cliente
POST   /api/v1/customers/login     - Iniciar sesión
GET    /api/v1/customers/:id       - Obtener cliente
PUT    /api/v1/customers/:id       - Actualizar cliente
DELETE /api/v1/customers/:id       - Desactivar cliente
```

### Ticket Purchases
```
POST   /api/v1/purchases                           - Crear compra
GET    /api/v1/purchases/:id                       - Obtener compra
GET    /api/v1/purchases/customer/:customerId      - Compras por cliente
GET    /api/v1/purchases/movie/:movieId            - Compras por película
POST   /api/v1/purchases/:id/confirm               - Confirmar compra
POST   /api/v1/purchases/:id/cancel                - Cancelar compra
```

## Dependencies

### Spring Boot
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-security
- spring-boot-starter-mail

### Database
- postgresql

### Authentication
- jjwt (JSON Web Tokens)

### Cloud Storage
- aws-java-sdk-s3

### Utilities
- lombok

## Security

- **JWT Authentication**: Tokens JWT para autenticación stateless
- **Password Encoding**: BCrypt para encriptación de contraseñas
- **CORS**: Configuración de CORS para requests desde frontend
- **Input Validation**: Validación de entrada con Jakarta Bean Validation

## Development

### Code Style
- Usar nombres descriptivos en inglés
- Incluir documentación JavaDoc en clases públicas
- Seguir conveción PascalCase para clases, camelCase para métodos
- Una responsabilidad por clase (Single Responsibility Principle)

### Naming Conventions
- Entidades: `NounEntity` (e.g., `Movie`)
- Servicios: `INounService` (interface) y `NounServiceImpl` (implementación)
- Repositories: `NounRepository`
- Controllers: `NounController`
- DTOs: `NounDTO` o `ActionNounDTO`

## Monitoring

Logs se configuran en `src/main/resources/logback-spring.xml`

## Deployment

Para producción:
1. Cambiar `ddl-auto` a `validate`
2. Configurar variables de entorno
3. Usar base de datos PostgreSQL en servidor
4. Configurar AWS S3 para almacenamiento de imágenes

## Troubleshooting

### Connection to database failed
- Verificar que PostgreSQL esté corriendo
- Verificar credenciales en application.yml
- Verificar que la base de datos exista

### JWT Token validation failed
- Verificar que `jwt.secret` sea consistente
- Verificar que el token no haya expirado

## Contributing

Por favor seguir:
1. Clean Code principles
2. Design Patterns (Service, Repository, DTO)
3. Code documentation
4. Consistent naming

## License

Vortexbird © 2025
