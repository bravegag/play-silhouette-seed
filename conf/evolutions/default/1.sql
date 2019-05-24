# schema

# --- !Ups

CREATE TABLE "user" (
	id INT AUTO_INCREMENT NOT NULL,
	first_name VARCHAR(50),
	middle_name VARCHAR(50),
	last_name VARCHAR(50),
	date_of_birth DATE,
	username VARCHAR(100) NOT NULL,
	email VARCHAR(100) NOT NULL,
	avatar_url VARCHAR(200) NOT NULL,
	last_login TIMESTAMP DEFAULT NULL,
	active BOOLEAN NOT NULL DEFAULT FALSE,
	modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (id)
);

CREATE TABLE security_role (
    id INT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_security_role (
	user_id INT NOT NULL,
	security_role_id INT NOT NULL,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    FOREIGN KEY (security_role_id) REFERENCES security_role(id) ON DELETE CASCADE,
	PRIMARY KEY (user_id, security_role_id)
);

INSERT INTO security_role (name) values ('user');
INSERT INTO security_role (name) values ('administrator');

# --- !Downs

DROP TABLE user_security_role CASCADE;

DROP TABLE security_role CASCADE;

DROP TABLE "user" CASCADE;