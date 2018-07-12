# JDBI CDI Extension

A **CDI Extension** for injecting [JDBI](http://jdbi.org/) SQL Objects into CDI beans. This extension creates SQL Objects by using the Jdbi.onDemand() method. As described in the JDBI documentation this may have a performance penalty as connections are allocated and released on each invocation.

## Build

```bash
$ mvn clean install
```

## Usage

### Maven Dependency

```xml
<dependency>
  <groupId>io.github.patrikostberg.jdbi</groupId>
  <artifactId>jdbi3-cdi</artifactId>
  <version>0.0.1</version>
</dependency>
```

### JDBI Interface

The SQL Object interface needs to be annotated with the JdbiSqlObject annotionation.

```java
@JdbiSqlObject
public interface TestDao {
  @SqlQuery("select h2version() as version from dual")
  String getVersion();
}
```

### JDBI Producer

A JDBI producer is needed to create the Jdbi instance. The created object have to be scoped Dependent since the Jdbi object cannot be proxied.

```java
@ApplicationScoped
public class JdbiProvider {
  private Jdbi jdbi;
  
  @PostConstruct
  public void init() {
    jdbi = Jdbi.create("jdbc:h2:mem:test");
    jdbi.installPlugin(new SqlObjectPlugin());
  }

  @Produces
  @Default
  @Dependent
  public Jdbi createJdbi() {
    return jdbi;
  }
}
```

### Injection

```java
@Inject
private TestDao testDao;
```
