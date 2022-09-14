package io.github.patrikostberg.jdbi.v3.cdi.beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@ApplicationScoped
public class JdbiProvider {
  private Jdbi defaultJdbi;
  private Jdbi testJdbi;

  @Resource(lookup = "/datasource/defaultDS") 
  private DataSource defaultDs;

  @Resource(lookup = "/datasource/testDS") 
  private DataSource testDs;
  
  @PostConstruct
  public void init() {
    defaultJdbi = Jdbi.create(defaultDs);
    defaultJdbi.installPlugin(new SqlObjectPlugin());

    testJdbi = Jdbi.create(testDs);
    testJdbi.installPlugin(new SqlObjectPlugin());
  }

  @Produces
  @Default
  @Dependent
  public Jdbi createDefaultJdbi() {
    return defaultJdbi;
  }

  @Produces
  @TestQualifier
  @Dependent
  public Jdbi createTestJdbi() {
    return testJdbi;
  }
}
