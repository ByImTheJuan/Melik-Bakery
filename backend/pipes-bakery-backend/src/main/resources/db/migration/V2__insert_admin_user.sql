# This migration script inserts an admin user into the clients table.

INSERT INTO clients (
    email,
    first_name,
    last_name,
    password,
    role,
)
VALUES (
    'admin@melik.com',
    'Admin',
    'Melik',
    '$2a$10$zPxvKTY6QndsasqmBySir.DBEvPHe4trUa4ImlbEtT0.vMZOjlOB6',
    'ADMIN',
);