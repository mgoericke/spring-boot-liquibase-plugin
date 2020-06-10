# Spring Boot mit Liquibase

## Datenbank durch Spring Boot generieren lassen

* Liquibase ausschalten in application.yml
    * `spring.liquibase.enabled=false`
* Automatisches Generieren der Tabellen einschalten
    * `spring.jpa.generate-ddl=true`
* Datenbank starten
    * `docker-compose up`
* Applikation starten und Schema generieren lassen
    *  `gradlew bootRun`
    
## Changelog generieren lassen

* Liquibase Plugin aufrufen
    * `gradle generateChangelog`
* Prüfen ob changelog angelegt wurde:
    * `src/main/resources/db/changelog/01-initial-changelog.yaml`
    
## Migration durch Liquibase

* Liquibase einschalten in application.yml
    * `spring.liquibase.enabled=true`
* Automatisches Generieren der Tabellen ausschalten
    * `spring.jpa.generate-ddl=false`
* Erzeugte Datenbank löschen
    * `docker-compose down`
* leere Datenbank erneut starten
    * `docker-compose up`
* Applikation starten und Migration durch Liquibase durchführen lassen
    *  `gradlew bootRun`
* Die Tabellen sind nun in der Datenbank
    * fachliche Tabellen
    * Liquibase Maintenance Tabellen
        * Databasechangelog
        * databasechangeloglock


## Configuration

### Liquibase Properties

src/main/resources/liquibase.properties

> *TAKE CARE of the correct package name in the referenceUrl property!*<br/>
> liquibase.changelog.referenceUrl=hibernate:spring:{ENTITY-PACKAGE}?dialect=org.hibernate.dialect.PostgreSQLDialect


```properties
liquibase.changelog.main=src/main/resources/db/changelog/01-initial-changelog.yaml
liquibase.changelog.referenceUrl=hibernate:spring:de.javamark.springboot.liqubase.domain?dialect=org.hibernate.dialect.PostgreSQLDialect
datasource.url:jdbc:postgresql://localhost:5432/rainbow_database
datasource.username:unicorn_user
datasource.password:magical_password
datasource.driver-class-name:org.postgresql.Driver
```

### Liquibase Plugin - build.gradle
Plugin hinzufügen
```groovy
plugins {
    id 'org.liquibase.gradle' version '2.0.3'
}
```

### Dependencies - build.gradle
Dependencies einbinden
```groovy
dependencies {
    implementation 'org.liquibase:liquibase-core'
    liquibaseRuntime 'org.liquibase.ext:liquibase-hibernate5:3.8'
    liquibaseRuntime sourceSets.main.runtimeClasspath
    liquibaseRuntime sourceSets.main.output
}
```

### Liquibase Properties einlesen - build.gradle

Properties einlesen, zuweisen und liquibase Gradle Tasks an den Aufruf von gradle assemble knüpfen.

```groovy
def props = new Properties()
file("src/main/resources/liquibase.properties").withInputStream { props.load(it) }
diff.dependsOn assemble
diffChangeLog.dependsOn assemble

liquibase {
    activities {
        main {
            changeLogFile props.getProperty("liquibase.changelog.main")
            referenceUrl props.getProperty("liquibase.changelog.referenceUrl")
            url props.getProperty("datasource.url")
            username props.getProperty("datasource.username")
            password props.getProperty("datasource.password")
            driver props.getProperty("datasource.driver-class-name")
            referenceDriver "liquibase.ext.hibernate.database.connection.HibernateDriver"
        }
    }
}
```

