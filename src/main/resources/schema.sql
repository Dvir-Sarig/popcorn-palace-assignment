CREATE TABLE IF NOT EXISTS movies (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    genre VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    rating DECIMAL(3,1) NOT NULL,
    release_year INT NOT NULL
);

CREATE TABLE IF NOT EXISTS showtimes (
    id SERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    theater VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id UUID PRIMARY KEY,
    showtime_id BIGINT NOT NULL,
    seat_number INT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
);
