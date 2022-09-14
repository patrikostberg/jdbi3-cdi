package io.github.patrikostberg.jdbi.v3.cdi.dao;

import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface TestDao {
  @SqlQuery("select user")
  String getVersion();
}
