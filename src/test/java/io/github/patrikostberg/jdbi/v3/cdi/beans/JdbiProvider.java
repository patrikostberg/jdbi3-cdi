package io.github.patrikostberg.jdbi.v3.cdi.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

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
