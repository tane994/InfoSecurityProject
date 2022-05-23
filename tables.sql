CREATE TABLE users (
	name varchar(50)  NOT NULL,
	surname varchar(50) NOT NULL,
	email varchar(50) NOT NULL,
	password varchar(50) NOT NULL,
	CONSTRAINT user_PK PRIMARY KEY (email)
);

CREATE TABLE mail (
	sender varchar(50) NOT NULL,
	receiver varchar(50) NOT NULL,
	subject varchar(100) NULL,
	body text NOT NULL,
	time timestamp NOT NULL,
	CONSTRAINT mail_FK FOREIGN KEY (sender) REFERENCES users (email),
	CONSTRAINT mail_FK_1 FOREIGN KEY (receiver) REFERENCES users(email)
);