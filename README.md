# JDBI CDI Extension

A **CDI Extension** for injecting [JDBI](http://jdbi.org/) handles and SQL objects. The extension creates a JDBI handle bean wrapped in the JdbiHandleProvider for each Jdbi bean and creates SQL object for detected SQL object interfaces. Created JDBI handles are transaction scoped and SQL Objects are created from the created JDBI handles. SQL objects beans are only created for beans which have injection points. The extension is qualifier aware.

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
  <version>0.1.1</version>
</dependency>
```

The extension does not depend on the JDBI SQL object asrtifact and needs to be included if SQL objects are to be used.

### JDBI Interface

The SQL object interface are detected by scanning interfaces for SQL object annotations on methods. The following interface would be detected due to the @SqlQuery annotation. Qualifiers cannot be used on the SQL object interfaces. Instead the extension automatically creates a SQL object bean when it finds a SQL object injection point with a qualifier. The SQL object is created from the JDBI handle with matching qualifiers.

```java
public interface TestDao {
  @SqlQuery("select h2version() as version from dual")
  String getVersion();
}
```

### JDBI Producer

A JDBI producer is needed to create the Jdbi instance. The created object have to be scoped Dependent since the Jdbi class cannot be proxied.
The extension support custom qualifiers. For each created JDBI bean a corresponding JDBI handle bean with identical qualifiers is created.

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
  @Default // Can be omitted or your custom qualifier.
  @Dependent
  public Jdbi createJdbi() {
    return jdbi;
  }
}
```

### Injection

```java
@Inject
private JdbiHandleProvider handleProvider;

// Or

@Inject
private TestDao testDao;
```
