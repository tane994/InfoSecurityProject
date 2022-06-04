CREATE TABLE users (
	name VARCHAR(50) NOT NULL,
	surname VARCHAR(50) NOT NULL,
	email VARCHAR(50) NOT NULL PRIMARY KEY,
	password VARCHAR(100) NOT NULL
);

CREATE TABLE keys (
    email VARCHAR(50) NOT NULL PRIMARY KEY,
    pubkey INTEGER NOT NULL,
    exponent INTEGER NOT NULL
);

CREATE TABLE tokens (
    email VARCHAR(50) NOT NULL,
    token VARCHAR(50) NOT NULL,
    PRIMARY KEY(email,token)
);

CREATE TABLE mails (
	sender VARCHAR(50) NOT NULL REFERENCES users(email),
	receiver VARCHAR(50) NOT NULL REFERENCES users(email),
	subject VARCHAR(100) NULL,
	body TEXT NOT NULL,
	"timestamp" TIMESTAMP NOT NULL PRIMARY KEY,
	digital_signature TEXT DEFAULT NULL
);