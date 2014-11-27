package at.risedev.vaadin.cdi.impl;

import at.risedev.vaadin.cdi.api.CdiUI;
import at.risedev.vaadin.cdi.api.UIScoped;
import com.vaadin.ui.UI;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class VaadinUIExtension implements Extension {

    private UIScopedContext context;

    private Map<String, Class<? extends UI>> uis = new HashMap<>();

    public void registerVaadinUIs(@Observes ProcessAnnotatedType<? extends UI> pat) {
        AnnotatedType<? extends UI> at = pat.getAnnotatedType();
        CdiUI viewAnnotation = at.getAnnotation(CdiUI.class);
        if (viewAnnotation != null) {
            uis.put(viewAnnotation.value(), pat.getAnnotatedType().getJavaClass());
        }
    }

    public void registerUIContext(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        context = new UIScopedContext(beanManager);
        abd.addContext(new ContextWrapper(context, UIScoped.class));
        abd.addContext(new ContextWrapper(context, CdiUI.class));
    }

    public UIScopedContext getContext() {
        return context;
    }


    public Map<String, Class<? extends UI>> getUis() {
        return uis;
    }

    private static final class ContextWrapper implements Context {

        private Context wrapped;
        private Class<? extends Annotation> scope;

        public ContextWrapper(Context wrapped, Class<? extends Annotation> scope) {
            this.wrapped = wrapped;
            this.scope = scope;
        }

        @Override
        public Class<? extends Annotation> getScope() {
            return scope;
        }

        @Override
        public <T> T get(Contextual<T> component, CreationalContext<T> creationalContext) {
            return wrapped.get(component, creationalContext);
        }

        @Override
        public <T> T get(Contextual<T> component) {
            return wrapped.get(component);
        }

        @Override
        public boolean isActive() {
            return wrapped.isActive();
        }
    }
}
