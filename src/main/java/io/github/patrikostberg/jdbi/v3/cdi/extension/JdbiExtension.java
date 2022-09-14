package io.github.patrikostberg.jdbi.v3.cdi.extension;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.patrikostberg.jdbi.v3.cdi.JdbiHandleProvider;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessBean;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;
import jakarta.transaction.TransactionScoped;

public class JdbiExtension implements Extension {
    private static final Class<? extends Annotation> SCOPE = TransactionScoped.class;
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbiExtension.class);

    private final Map<Class<?>, Set<Set<Annotation>>> sqlObjects;
    private final Set<Set<Annotation>> handles;

    public JdbiExtension() {
      sqlObjects = new HashMap<>();
      handles = new HashSet<>();
    }

    public <X> void processBean(@Observes ProcessBean<X> event) {
        if (event.getAnnotated().getBaseType() instanceof Class && Jdbi.class.isAssignableFrom((Class<?>)event.getAnnotated().getBaseType())) {
            handles.add(event.getBean().getQualifiers());
        }
    }
    
    public <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event) {
        AnnotatedType<?> type = event.getAnnotatedType();
        
        if (isSqlObject(type.getJavaClass())) {
            sqlObjects.put(type.getJavaClass(), new HashSet<>());
            event.veto();
        }
    }

    public <X, T> void processInjectionPoint(@Observes ProcessInjectionPoint<X, T> event) {
        if (event.getInjectionPoint().getType() instanceof Class && sqlObjects.containsKey(event.getInjectionPoint().getType())) {
            sqlObjects.get(event.getInjectionPoint().getType()).add(event.getInjectionPoint().getQualifiers());
        }
    }
      
    public void addMapperBeans(@Observes final AfterBeanDiscovery abd, final BeanManager beanManager) {
        handles.forEach((qualifiers) -> {
            LOGGER.debug("Creating JDBI handle bean with qualifiers: {}", qualifiers);
            
            abd.addBean()
                .id(JdbiHandleProvider.class.getName())
                .beanClass(JdbiHandleProvider.class)
                .scope(SCOPE)
                .qualifiers(qualifiers)
                .types(JdbiHandleProvider.class, Object.class)
                .destroyWith((instance, ctx) -> ((JdbiHandleProvider)instance).getJdbiHandle().close())
                .produceWith(instance -> {
                    Instance<Jdbi> jdbiRef = instance.select(Jdbi.class, qualifiers.toArray(new Annotation[0]));
                    
                    //return jdbiRef.get().open();
                    return new JdbiHandleProviderImpl(jdbiRef.get().open());
                });
        });
        handles.clear();
        
        sqlObjects.forEach((beanClass, beanQualifiers) -> {
            beanQualifiers.forEach(qualifiers -> {
                LOGGER.debug("Creating JDBI sql object of type '{}' with qualifiers: {}", beanClass.getName(), qualifiers);

                if (!qualifiers.isEmpty()) {
                    abd.addBean()
                        .id(beanClass.getName())
                        .beanClass(beanClass)
                        .scope(SCOPE)
                        .qualifiers(qualifiers)
                        .types(beanClass, Object.class)
                        .produceWith(instance -> {
                            //Instance<Handle> handleRef = instance.select(Handle.class, qualifiers.toArray(new Annotation[0]));

                            //return handleRef.get().attach(beanClass);

                            Instance<JdbiHandleProvider> handleRef = instance.select(JdbiHandleProvider.class, qualifiers.toArray(new Annotation[0]));
                            return handleRef.get().getJdbiHandle().attach(beanClass);
                        });
                }
            });
        });
        sqlObjects.clear();
    }

    private boolean isSqlObject(Class<?> clz) {
        return Arrays.stream(clz.getDeclaredMethods()).anyMatch(method ->
            Arrays.stream(method.getAnnotations()).anyMatch(ann -> ann.annotationType().getPackageName().startsWith("org.jdbi.v3.sqlobject")));
    }

    private static class JdbiHandleProviderImpl implements JdbiHandleProvider {
        private final Handle handle;

        private JdbiHandleProviderImpl(Handle handle) {
            this.handle = handle;
        }

        @Override
        public Handle getJdbiHandle() {
            return handle;
        }
    }
}
