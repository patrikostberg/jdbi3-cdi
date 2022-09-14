package io.github.patrikostberg.jdbi.v3.cdi.beans;

import io.github.patrikostberg.jdbi.v3.cdi.JdbiHandleProvider;
import io.github.patrikostberg.jdbi.v3.cdi.dao.TestDao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestApp {
  @Inject
  private JdbiHandleProvider defaultHandleProvider;

  @Inject
  private TestDao defaultTestDao;

  @Inject
  @TestQualifier
  private JdbiHandleProvider testHandleProvider;

  @Inject
  @TestQualifier
  private TestDao testTestDao;

  public String getVersionDefaultHandle() {
    return defaultHandleProvider.getJdbiHandle().createQuery("select user").mapTo(String.class).first();
  }

  public String getVersionDefaultDao() {
    return defaultTestDao.getVersion();
  }

  public String getVersionTestHandle() {
    return testHandleProvider.getJdbiHandle().createQuery("select user").mapTo(String.class).first();
  }

  public String getVersionTestDao() {
    return testTestDao.getVersion();
  }
}
