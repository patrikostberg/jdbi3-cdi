package io.github.patrikostberg.jdbi.v3.cdi;

import org.jdbi.v3.core.Handle;

public interface JdbiHandleProvider {
    Handle getJdbiHandle();
}
