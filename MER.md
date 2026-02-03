```mermaid
erDiagram
    CUSTOMERS ||--o{ TICKET_PURCHASES : "realiza"
    MOVIES ||--o{ TICKET_PURCHASES : "se vende en"
    
    CUSTOMERS {
        bigint id PK "Primary Key"
        varchar(100) email UK "Unique - Email del cliente"
        varchar(20) phone "Teléfono"
        varchar(100) first_name "Nombre"
        varchar(100) last_name "Apellido"
        varchar(255) password "Password encriptado (BCrypt)"
        varchar(20) role "ADMIN o CUSTOMER"
        boolean is_enabled "Estado activo/inactivo"
        timestamp created_at "Fecha de registro"
    }
    
    MOVIES {
        bigint id PK "Primary Key"
        varchar(255) title "Título de la película"
        text description "Descripción completa"
        varchar(1000) image_url "URL de AWS S3"
        integer duration_minutes "Duración en minutos"
        varchar(100) genre "ACCION, COMEDIA, DRAMA, etc"
        double price "Precio unitario"
        boolean is_enabled "Visible/oculta"
    }
    
    TICKET_PURCHASES {
        bigint id PK "Primary Key"
        bigint customer_id FK "FK -> customers"
        bigint movie_id FK "FK -> movies"
        integer quantity "Cantidad de boletas"
        double unit_price "Precio unitario al momento"
        double total_amount "quantity * unit_price"
        varchar(20) status "CONFIRMED, PENDING, CANCELLED, REFUNDED"
        varchar(4) card_last_four "Últimos 4 dígitos tarjeta"
        varchar(200) card_holder_name "Titular de la tarjeta"
        timestamp purchase_date "Fecha y hora de compra"
        varchar(50) confirmation_code UK "Código único generado"
    }
```

## Descripción de Relaciones

### CUSTOMERS → TICKET_PURCHASES (1:N)
- Un cliente puede realizar múltiples compras
- Cada compra pertenece a un único cliente
- Cascade: No eliminar cliente si tiene compras
- FK: `customer_id` → `customers.id`

### MOVIES → TICKET_PURCHASES (1:N)
- Una película puede tener múltiples ventas
- Cada compra es de una única película
- Cascade: No eliminar película si tiene ventas
- FK: `movie_id` → `movies.id`

## Índices para Optimización

```sql
-- CUSTOMERS
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_enabled ON customers(is_enabled);
CREATE INDEX idx_customers_role ON customers(role);

-- MOVIES
CREATE INDEX idx_movies_enabled ON movies(is_enabled);
CREATE INDEX idx_movies_genre ON movies(genre);
CREATE INDEX idx_movies_enabled_genre ON movies(is_enabled, genre);

-- TICKET_PURCHASES
CREATE INDEX idx_purchases_customer ON ticket_purchases(customer_id);
CREATE INDEX idx_purchases_movie ON ticket_purchases(movie_id);
CREATE INDEX idx_purchases_date ON ticket_purchases(purchase_date);
CREATE INDEX idx_purchases_confirmation ON ticket_purchases(confirmation_code);
```

## Datos de Ejemplo

### Admin por defecto
```sql
INSERT INTO customers VALUES 
(1, 'admin@vortexbird.com', '3001234567', 'Admin', 'Sistema', 
'$2a$10$encrypted...', 'ADMIN', true, CURRENT_TIMESTAMP);
```

### Películas iniciales (6)
- Inception (ACCION)
- The Shawshank Redemption (DRAMA)
- The Dark Knight (ACCION)
- Pulp Fiction (CRIMEN)
- Forrest Gump (DRAMA)
- The Matrix (CIENCIA_FICCION)

### Clientes de ejemplo (6)
- Juan Pérez (CUSTOMER)
- María García (CUSTOMER)
- Carlos López (CUSTOMER)
- Ana Martínez (CUSTOMER)
- Luis Rodríguez (CUSTOMER)
- Sofia Hernández (CUSTOMER)

### Compras de ejemplo (7)
- Estados: CONFIRMED, PENDING
- Cantidades variadas (1-4 boletas)
- Códigos únicos: CONF-xxx-xxx
