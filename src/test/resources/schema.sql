create table users
(
    id       INT auto_increment primary key,
    email    varchar(64) not null unique,
    password varchar(64) not null,
    role varchar(64) not null
);
