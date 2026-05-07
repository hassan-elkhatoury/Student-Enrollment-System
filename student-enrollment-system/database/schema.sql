CREATE DATABASE IF NOT EXISTS student_service_db;
CREATE DATABASE IF NOT EXISTS course_service_db;
CREATE DATABASE IF NOT EXISTS enrollment_service_db;

USE student_service_db;

CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cnie VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

USE course_service_db;

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(600) NOT NULL,
    credits INT NOT NULL
);

USE enrollment_service_db;

CREATE TABLE IF NOT EXISTS enrollments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    enrollment_date DATETIME NOT NULL
);
