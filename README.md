# Information Security Project

## Requirements

- Java JDK >16
- Maven
- Tomcat 10
- Postgresql

## Running

1) Copy `conf/database.properties` in `entities/src/main/resources` and configure it accordingly with postgres credentials
   
2) Copy `conf/settings.xml` to `<user_home>/.m2` and configure it accordingly with tomcat's credentials (admin and no password by default). See [here](https://maven.apache.org/settings.html) and [here](https://tomcat.apache.org/maven-plugin-2.0/tomcat7-maven-plugin/usage.html) for details about how to setup such file
 
3) Start tomcat and postgres. Note that we expect tomcat to run on port `8080` and postgres on port `5432`.

4) Execute the following: *(note that two separated .war files needs to be created)*
    ```bash
    mvn install
    mvn -f server/ compile tomcat7:deploy
    mvn -f client/ compile tomcat7:deploy
    ```

5) The app should have been deployed. Note that the database is automatically populated when the first query gets executed. In any case, the tables structure is found [here](entities/src/main/resources/tables.sql)

## Implementation

The application is divided into two separate web bundles:
- `server` manages the database and exposes a JSON API. It is accessible at [http://localhost:8080/securityApi](http://localhost:8080/securityApi);
- `client` contains the HTML front-end and performs operations regarding encryption and decryption. It is accessible at  [http://localhost:8080/security](http://localhost:8080/security).

## Applied security measures

- **SQL Injections** are prevented in the server with Java *prepared statements* *(see [`Db`](entities/src/main/java/it/unibz/emails/entities/persistence/Db.java))*;
- **XSS Injections** are instead fixed both on client and server by sanitizing all input fields prior utilization (see [`BaseServlet`](client/src/main/java/it/unibz/emails/client/BaseServlet.java) and [`Sanitizer`](server/src/main/java/it/unibz/emails/Sanitizer.java). Particularly, dangerous tags like `script` or `onclick` values are removed with the aid of  *JSoup*, since the possible combinations are quite long to be manually declared;
- **CSRF** is resolved by generating a random token that is then stored both on server (in session) and client (as a cookie). Whenever a form is sent, we verify that the two values match *(see [`BaseServlet`](client/src/main/java/it/unibz/emails/client/BaseServlet.java))*;
- **Passwords security** has been achieved with encryption, particularly using the key-stretching algorithm [*BCrypt*](entities/src/main/java/it/unibz/emails/entities/persistence/Password.java). Also in this case, we used a third-party library.

Additionally, a number of safety measures have been implemented:
- input fields are **validated** both on [client](client/src/main/webapp/register.jsp) *(using html attributes)* and [server](server/src/main/java/it/unibz/emails/endpoints/Register.java) *(using jakarta bean validation)* before using their values. For example, we check that the email field is in the correct format;
- a number of [**http headers**](client/src/main/java/it/unibz/emails/client/Headers.java) is sent, so that browsers can read them and limit functionalities of the website;
- the user state is kept within a **session** instead of servlet attributes. At logout, the session is properly terminated;
- the **database credentials** are not saved in the source code in the git repository, rather they live in a local file;
- some common **mistakes** are reported in the html page instead of crashing the application

### RSA Algorithm

After registering, the three *RSA parameters* (d, e, n) are generated and stored differently according to their function: public key and exponent are stored inside the *keys* table. Private keys are instead memorized in the browser local storage and retrieved when needed with the help of a simple [javascript file](client/src/main/webapp/script.js).

The core idea is that just servlets in the *client* package have access to the private key, while the ones in the *server* package never have such key. This is highlighted by the fact that client and server are effectively two separated entities which communicate through standard HTTP messages. Whenever using it becomes necessary, for example to sign a message, the *client* side will retrieve the private key and use it locally for the desired operation. The *server* servlet will just receive the result of such computation.

#### Message Encryption

Encryption is done in the following steps: for each character of the message,
- the integer ASCII representation of the typed character is retrieved;
- it is then encrypted with the formula `pow(ascii,e)) mod n`, where `e` is the public key of the receiver.

To more easily store the cipher-text in the database, the array of encrypted positions is encoded in the *Base64* format.

Decryption works in an opposite manner:
- Base64 is reversed and the encrypted array is retrieved
- the plain-text representation is obtained with the formula `pow(enc,d)) mod n`, where `d` is the private key of the receiver;
- The original letter is retrieved by following the ASCII table

#### Digital Signature

Digital signature is instead implemented as follows:
- the plain-text body is hashed with SHA-256,
- it is encrypted with the private key of the sender.

At reception, the signature is decrypted by using the public key of the sender. A new hash is then computed on the decrypted body and it is verified whether the decrypted signature matches the computed hash.
