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

-- Insert sample customer (password: Test1234)
INSERT INTO customers (email, phone, first_name, last_name, password, is_enabled, created_at) VALUES
('test@example.com', '3001234567', 'Test', 'User', '$2a$10$rN7pVQgLbVLmPm1P5KyZPu1xJ0XrLrYGFJqJ5YZmvZvQX6nzqZvQa', true, CURRENT_TIMESTAMP);

