package io.github.patrikostberg.jdbi.v3.cdi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for marking a interface as a JDBI SQL Object.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface JdbiSqlObject {
}
