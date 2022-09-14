package io.github.patrikostberg.jdbi.v3.cdi.dao;

import org.jdbi.v3.sqlobject.statement.SqlQuery;

public interface TestDao {
  @SqlQuery("select current_user() as current_user from dual")
  String getVersion();
}
