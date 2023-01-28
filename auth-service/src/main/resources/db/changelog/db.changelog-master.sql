-- liquibase formatted sql

-- changeset author: sergey

create table roles
(
    id         UUID primary key,
    name       varchar(255) unique not null,
    created_at TIMESTAMP default current_timestamp,
    updated_at TIMESTAMP default current_timestamp
);

create table users
(
    id          UUID primary key,
    username    varchar(255) not null unique,
    password    varchar(255) not null,
    email       varchar(255) not null,
    company_vat varchar(255) not null,
    created_at  TIMESTAMP default current_timestamp,
    updated_at  TIMESTAMP default current_timestamp
);

create table users_roles
(
    user_id    UUID not null references users (id),
    role_id    UUID not null references roles (id),
    created_at TIMESTAMP default current_timestamp,
    updated_at TIMESTAMP default current_timestamp,
    primary key (user_id, role_id)
);

insert into users (id, username, password, email, company_vat)
values ('ec2aa7f4-42d5-442c-8baf-6d4cc6d15db3', 'admin', '$2a$12$i2wJXQxdTdUMK7Zg5TSrX.LLuqI0SMKeiNmP3xdF3UEeaSipB6Nqa',
        'admin@admin.com', '7777777777'),
       ('7bd8952f-00f9-4354-b0b7-1152a1a6bcca', 'aberezhnoy', '$2a$12$7F/pB15Uv9t09gu/WI8Pe.dDeObpEMv5./4RRgDQiy/IkmDXkh0Sy',
        'admin@admin.com', '0123456789'),
       ('fa6f0504-54bd-42b1-8363-7970492e724d', 'abrazacs',
        '$2a$12$NfP7TBABXE1TzpoTfEjlAOoFGmPAJe6HVyGc3DSjuuaUWcb6WOyC2', 'sergeysemenov87@gmail.com', '6767676767'),
       ('9343e308-0dda-4b1a-bf8b-64819eee192e', 'nonepostlife',
        '$2a$12$NfP7TBABXE1TzpoTfEjlAOoFGmPAJe6HVyGc3DSjuuaUWcb6WOyC2', 'mail@mail.com', '1234567891');

insert into roles (id, name)
values ('f3db87e9-738e-46c8-990f-30206f38131a', 'ROLE_USER'),
       ('0b103c7e-9110-43cf-8236-4db3f12095c6', 'ROLE_ADMIN');

insert into users_roles (user_id, role_id)
values ('ec2aa7f4-42d5-442c-8baf-6d4cc6d15db3', 'f3db87e9-738e-46c8-990f-30206f38131a'),
       ('ec2aa7f4-42d5-442c-8baf-6d4cc6d15db3', '0b103c7e-9110-43cf-8236-4db3f12095c6'),
       ('fa6f0504-54bd-42b1-8363-7970492e724d', 'f3db87e9-738e-46c8-990f-30206f38131a'),
       ('7bd8952f-00f9-4354-b0b7-1152a1a6bcca', 'f3db87e9-738e-46c8-990f-30206f38131a');

