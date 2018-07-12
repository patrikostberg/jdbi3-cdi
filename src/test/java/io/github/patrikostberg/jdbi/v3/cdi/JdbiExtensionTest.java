package io.github.patrikostberg.jdbi.v3.cdi;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.patrikostberg.jdbi.v3.cdi.dao.TestDao;

@RunWith(Arquillian.class)
public class JdbiExtensionTest {
  @Inject
  private TestDao testDao;
  
  @Deployment
  public static JavaArchive createDeployment() {
      return ShrinkWrap.create(JavaArchive.class)
          .addPackages(true, JdbiExtensionTest.class.getPackage())
          .addAsResource("META-INF/beans.xml");
  }
  
  @Test
  public void testInjectNotNull() {
    Assert.assertNotNull(testDao);
  }
  
  @Test
  public void testDummy() {
    Assert.assertEquals("1.4.196", testDao.getVersion());
  }
}
