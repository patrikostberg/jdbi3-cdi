package io.github.patrikostberg.jdbi.v3.cdi;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionScoped;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import io.github.patrikostberg.jdbi.v3.cdi.beans.JdbiProvider;
import io.github.patrikostberg.jdbi.v3.cdi.beans.TestApp;
import io.github.patrikostberg.jdbi.v3.cdi.dao.TestDao;
import io.github.patrikostberg.jdbi.v3.cdi.extension.JdbiExtension;

@EnableWeld
public class JdbiExtensionTest {
  @WeldSetup
  public WeldInitiator weld = WeldInitiator.from(JdbiExtension.class, TestApp.class, TestDao.class, JdbiProvider.class)
      .activate(RequestScoped.class, TransactionScoped.class)
      .bindResource("/datasource/defaultDS", createDataSource("default-user"))
      .bindResource("/datasource/testDS", createDataSource("test-user"))
      .build();

  private DataSource createDataSource(String user) {
    JdbcDataSource ds = new JdbcDataSource();
    ds.setURL("jdbc:h2:mem:test");
    ds.setUser(user);

    return ds;
  }

  @Inject
  private TestApp testApp;
  
  @Test
  public void testDefaultHandle() {
    assertEquals("DEFAULT-USER", testApp.getVersionDefaultHandle());
  }

  @Test
  public void testDefaultDao() {
    assertEquals("DEFAULT-USER", testApp.getVersionDefaultDao());
  }

  @Test
  public void testTestHandle() {
    assertEquals("TEST-USER", testApp.getVersionTestHandle());
  }

  @Test
  public void testTestDao() {
    assertEquals("TEST-USER", testApp.getVersionTestDao());
  }
}
