# Information Security Project

## Requirements

- Java JDK >14
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