package io.github.patrikostberg.jdbi.v3.cdi.extension;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.patrikostberg.jdbi.v3.cdi.JdbiSqlObject;

/**
 * Extension for detecting and generating JDBI SQL Objects.
 */
public class JdbiExtension implements Extension {
  private final Set<AnnotatedType<?>> beans;
  
  private final static Logger logger = LoggerFactory.getLogger(JdbiExtension.class);
  
  public JdbiExtension() {
    beans = new HashSet<>();
  }
  
  public <X> void processAnnotatedType(@Observes @WithAnnotations({ JdbiSqlObject.class }) ProcessAnnotatedType<X> event) {
    AnnotatedType<?> type = event.getAnnotatedType(); 
    logger.info("Found JDBI sql object: {}", type.getJavaClass().getName());
    beans.add(type);
    event.veto();
  }
  
  public void addMapperBeans(@Observes final AfterBeanDiscovery abd, final BeanManager beanManager) {
    logger.info("Found {} JDBI sql objects.", beans.size());
    beans.forEach(bean -> {
      abd.addBean(new JdbiBean(bean, beanManager));
    });
    beans.clear();
  }
}
