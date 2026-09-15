CREATE TABLE vehicles (
                          id BIGSERIAL PRIMARY KEY,
                          brand VARCHAR(100) NOT NULL,
                          model VARCHAR(100) NOT NULL,
                          color VARCHAR(50) NOT NULL,
                          year INTEGER NOT NULL,
                          description VARCHAR(250),
                          price NUMERIC(12,2) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL
);

CREATE TABLE sales (
                       id BIGSERIAL PRIMARY KEY,
                       vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
                       buyer_id VARCHAR(100) NOT NULL,
                       total_amount NUMERIC(12,2) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);