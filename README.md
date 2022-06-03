# Information Security Project

## Requirements

- Java JDK >16
- Tomcat 10
- Postgresql

## Running

1) Copy `conf/database.properties` in `src/main/resources` and configure it accordingly with postgres credentials
   
2) Copy `conf/settings.xml` to `<user_home>/.m2` and configure it accordingly with tomcat's credentials (admin and no password by default). Alternatively, use the IDE's feature to achieve the same
 
3) Start tomcat and postgres

4) Execute the following:
    ```bash
    mvn compile
    mvn tomcat7:deploy  # Only if settings.xml has been used
    ```

5) The app should have been deployed at [http://localhost:8080/security](http://localhost:8080/security). Note that the database is automatically populated when the first query gets executed. The tables structure is found in `src/main/resources/tables.sql`

## Applied security measures

- **SQL Injections** are prevented with Java *prepared statements* (see [`Db`](src/main/java/it/unibz/emails/server/persistence/Db.java));
- **XSS Injections** are instead fixed by sanitizing all input fields prior utilization (see [`BaseServlet`](src/main/java/it/unibz/emails/server/servlet/BaseServlet.java)). Particularly, dangerous tags like `script` or `onclick` values are removed with the aid of  *JSoup*, since the possible combinations are quite long to be manually declared;
- **CSRF** is resolved by generating a random token that is then stored both on server and client (as a cookie). Whenever a form is sent, we verify that the two values match;
- **Passwords security** has been achieved with encryption, particularly using the key-stretching algorithm *BCrypt*. Also in this case, we used a third-party library.

Additionally, a number of safety measures have been implemented:
- input fields are **validated** before using their values. For example, we check that the email field is in the correct format;
- a number of **http headers** is sent, so that browsers can read them and limit functionalities of the website;
- the user state is kept within a **session** instead of servlet attributes. At logout, the session is now properly terminated;
- the **database credentials** are not saved in the source code in the git repository, rather they live in a local file;
- some common **mistakes** are reported in the html page instead of crashing the application

### RSA Algorithm

After registering, the three *RSA parameters* (d, e, n) are generated and stored differently according to their function: public key and exponent are stored inside the *users* table. Private keys are instead memorized in a separate database relation so to simulate isolation from other data. Functionalities that require using the private key are also executed by the separate servlets `KeysGenerationServlet` and `PrivKeyServlet`, so to mock a local client execution. 

The core idea is that just servlets in the *client* package have access to the private key. The ones in the *server* package, instead, never have such key. Whenever using it becomes necessary, for example to decrypt the inbox, an HTTP request is made to the *client* servlet, which will retrieve the private key and use it for the desired operation. The *server* servlet will just receive the result of such computation.

#### Message Encryption

Encryption is done in the following steps: for each character of the message,
- the integer ASCII representation of the typed character is retrieved;
- it is then encrypted with the formula `pow(pos,e)) mod n`, where `e` is the public key of the receiver.

To more easily store the cipher-text in the database, the array of encrypted positions is encoded in the *Base64* format.

#### Digital Signature

Digital signature is instead implemented as follows:
-  the plain-text body is hashed with SHA-256,
- it is encrypted with the private key of the sender.

At reception, the body is decrypted with the private key of the receiver. Also the signature is decrypted, but by using the public key of the sender. After that, a new hash is computed on the decrypted body and it is verified wether the decrypted signature matches the computed hash.
