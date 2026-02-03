-- Initial database setup for Movie Ticket System

-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    genre VARCHAR(100) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    is_enabled BOOLEAN DEFAULT true
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'CUSTOMER' NOT NULL,
    is_enabled BOOLEAN DEFAULT true NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create ticket_purchases table
CREATE TABLE IF NOT EXISTS ticket_purchases (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DOUBLE PRECISION NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) NOT NULL,
    card_last_four VARCHAR(4),
    card_holder_name VARCHAR(200),
    purchase_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmation_code VARCHAR(50) UNIQUE,
    CONSTRAINT fk_purchase_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_purchase_movie FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_enabled ON customers(is_enabled);
CREATE INDEX IF NOT EXISTS idx_customers_role ON customers(role);
CREATE INDEX IF NOT EXISTS idx_purchases_customer ON ticket_purchases(customer_id);
CREATE INDEX IF NOT EXISTS idx_purchases_movie ON ticket_purchases(movie_id);
CREATE INDEX IF NOT EXISTS idx_purchases_confirmation ON ticket_purchases(confirmation_code);
CREATE INDEX IF NOT EXISTS idx_movies_enabled ON movies(is_enabled);
CREATE INDEX IF NOT EXISTS idx_movies_genre ON movies(genre);

-- Insert sample movies
INSERT INTO movies (title, description, image_url, duration_minutes, genre, price, is_enabled) VALUES
('Avengers: Endgame', 'Los Vengadores restantes deben encontrar una manera de recuperar a sus aliados para un enfrentamiento épico con Thanos.', 'https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg', 181, 'Acción', 15000, true),
('El Padrino', 'El patriarca de una dinastía del crimen organizado transfiere el control de su imperio clandestino a su reacio hijo menor.', 'https://image.tmdb.org/t/p/w500/3bhkrj58Vtu7enYsRolD1fZdja1.jpg', 175, 'Drama', 12000, true),
('Inception', 'Un ladrón que roba secretos corporativos a través del uso de la tecnología de compartir sueños.', 'https://image.tmdb.org/t/p/w500/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg', 148, 'Ciencia Ficción', 14000, true),
('The Dark Knight', 'Cuando la amenaza conocida como el Joker emerge de su misterioso pasado, causa estragos y caos en la gente de Gotham.', 'https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg', 152, 'Acción', 13500, true),
('Pulp Fiction', 'Las vidas de dos sicarios de la mafia, un boxeador, la esposa de un gángster y dos bandidos se entrelazan en cuatro historias de violencia y redención.', 'https://image.tmdb.org/t/p/w500/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg', 154, 'Drama', 11000, true);

-- Insert sample customers (passwords are BCrypt hashed)
-- Original passwords for testing:
--   admin@movieticket.com: Admin1234
--   cliente@example.com: Cliente1234
--   maria.garcia@email.com: Maria1234
--   carlos.lopez@email.com: Carlos1234
--   ana.martinez@email.com: Ana1234
INSERT INTO customers (email, phone, first_name, last_name, password, role, is_enabled, created_at) VALUES
('admin@movieticket.com', '3001234567', 'Admin', 'Sistema', '$2a$10$CWJWU.1or/vQbGC3toRltu//21zpsetQP57spEFfxEuCeVV5vk.ii', 'ADMIN', true, CURRENT_TIMESTAMP),
('cliente@example.com', '3009876543', 'Cliente', 'Demo', '$2a$10$vI19TdCxpcfFAHcFpJJnXeU6lY14tPE22hsjOn9d05cdY2w4XpXIS', 'CUSTOMER', true, CURRENT_TIMESTAMP),
('maria.garcia@email.com', '3101234567', 'María', 'García', '$2a$10$kUd6juPHiYE.qCCbHA58kO3i/LkX2HoZfuatAdlJDcDgxZwKEs0ZO', 'CUSTOMER', true, CURRENT_TIMESTAMP),
('carlos.lopez@email.com', '3202345678', 'Carlos', 'López', '$2a$10$3i3BRc9gQSuwm40LX3Z9ye1oxcWSRFf3GD3eyNxdWbg0M6gPUUOQu', 'CUSTOMER', true, CURRENT_TIMESTAMP),
('ana.martinez@email.com', '3303456789', 'Ana', 'Martínez', '$2a$10$nluKXQOd3RTLxSrYfs.ciu/KU18VaPHi0cvlX4s6MbVUTue9kAwqu', 'CUSTOMER', true, CURRENT_TIMESTAMP);

-- Insert sample ticket purchases
INSERT INTO ticket_purchases (customer_id, movie_id, quantity, unit_price, total_amount, status, card_last_four, card_holder_name, purchase_date, confirmation_code) VALUES
-- Cliente Demo compra boletas para Avengers
(2, 1, 2, 15000, 30000, 'CONFIRMED', '1234', 'CLIENTE DEMO', CURRENT_TIMESTAMP - INTERVAL '5 days', 'CONF-001-2024'),
-- María García compra para Inception
(3, 3, 3, 14000, 42000, 'CANCELLED', '5678', 'MARIA GARCIA', CURRENT_TIMESTAMP - INTERVAL '3 days', 'CONF-002-2024'),
-- Carlos López compra para The Dark Knight
(4, 4, 1, 13500, 13500, 'CONFIRMED', '9012', 'CARLOS LOPEZ', CURRENT_TIMESTAMP - INTERVAL '2 days', 'CONF-003-2024'),
-- Ana Martínez compra para El Padrino
(5, 2, 4, 12000, 48000, 'CONFIRMED', '3456', 'ANA MARTINEZ', CURRENT_TIMESTAMP - INTERVAL '1 day', 'CONF-004-2024'),
-- María García compra nuevamente para Pulp Fiction
(3, 5, 2, 11000, 22000, 'CONFIRMED', '5678', 'MARIA GARCIA', CURRENT_TIMESTAMP - INTERVAL '12 hours', 'CONF-005-2024'),
-- Carlos López compra para Avengers
(4, 1, 2, 15000, 30000, 'CANCELLED', '9012', 'CARLOS LOPEZ', CURRENT_TIMESTAMP - INTERVAL '6 hours', 'CONF-006-2024'),
-- Cliente Demo compra para Inception
(2, 3, 1, 14000, 14000, 'CONFIRMED', '1234', 'CLIENTE DEMO', CURRENT_TIMESTAMP - INTERVAL '2 hours', 'CONF-007-2024');
