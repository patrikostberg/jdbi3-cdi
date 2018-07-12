package io.github.patrikostberg.jdbi.v3.cdi.dao;

import org.jdbi.v3.sqlobject.statement.SqlQuery;

import io.github.patrikostberg.jdbi.v3.cdi.JdbiSqlObject;

@JdbiSqlObject
public interface TestDao {
  @SqlQuery("select h2version() as version from dual")
  String getVersion();
}
