USE student_service_db;

INSERT INTO students (cnie, first_name, last_name, email) VALUES
('CD2387', 'Yassine', 'El Amrani', 'yassine.elamrani@uae.ac.ma'),
('AB1045', 'Salma', 'Bennani', 'salma.bennani@uae.ac.ma'),
('JK7721', 'Imane', 'Zahraoui', 'imane.zahraoui@uae.ac.ma'),
('EF5520', 'Omar', 'Rami', 'omar.rami@uae.ac.ma');

USE course_service_db;

INSERT INTO courses (title, description, credits) VALUES
('Spring Framework', 'Controllers, dependency injection, services and repositories.', 4),
('Data Mining', 'Data preparation, classification and knowledge extraction.', 3),
('Microservices Architecture', 'API Gateway, service discovery and service-to-service communication.', 4),
('Advanced Java', 'Java OOP, collections, streams and enterprise patterns.', 3);

USE enrollment_service_db;

INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES
(1, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 2, DATE_SUB(NOW(), INTERVAL 30 HOUR)),
(2, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR));
