
DELETE FROM reserved;
DELETE FROM client;
INSERT INTO reserved(reserved_date ) VALUES('2025-11-05');
INSERT INTO client(full_name, email ) VALUES('Admin', 'volcano.admin@gmail.com');