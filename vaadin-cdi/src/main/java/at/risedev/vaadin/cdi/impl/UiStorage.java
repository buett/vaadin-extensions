package at.risedev.vaadin.cdi.impl;

import com.vaadin.server.VaadinSession;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@SessionScoped
public class UiStorage implements Serializable {

    public Map<Integer, ContextualStorage> getContextualStorageMap() {
        return getInternalMap();
    }

    public ContextualStorage get(Integer uiId) {
        return getInternalMap().get(uiId);
    }

    public ContextualStorage put(Integer uiId, ContextualStorage newStorage) {
        return getInternalMap().putIfAbsent(uiId, newStorage);
    }


    /**
     *
     * This method will replace the storageMap and with
     * a new empty one.
     * This method can be used to properly destroy the WindowBeanHolder beans
     * without having to sync heavily. Any
     * {@link javax.enterprise.inject.spi.Bean#destroy(Object, javax.enterprise.context.spi.CreationalContext)}
     * should be performed on the returned old storage map.
     * @return the old storageMap.
     */
    public Map<Integer, ContextualStorage> forceNewStorage() {
        ConcurrentHashMap<Integer, ContextualStorage> oldStorageMap = getInternalMap();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        vaadinSession.setAttribute(UiStorage.class.getName(), new ConcurrentHashMap<Integer, ContextualStorage>());
        return oldStorageMap;
    }

    /**
     * This method properly destroys all current &#064;UIScoped beans
     * of the active session and also prepares the storage for new beans.
     * It will automatically get called when the session context closes
     * but can also get invoked manually, e.g. if a user likes to get rid
     * of all it's &#064;UIScoped beans.
     */
    @PreDestroy
    public void destroyBeans() {
        // we replace the old windowBeanHolder beans with a new storage Map
        // an afterwards destroy the old Beans without having to care about any syncs.
        Map<Integer, ContextualStorage> oldUiContextStorages = forceNewStorage();

        for (ContextualStorage contextualStorage : oldUiContextStorages.values()) {
            AbstractContext.destroyAllActive(contextualStorage);
        }
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<Integer, ContextualStorage> getInternalMap() {
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession == null) {
            throw new IllegalStateException("No active Vaadin session");
        }
        ConcurrentHashMap<Integer, ContextualStorage> map =
                (ConcurrentHashMap<Integer, ContextualStorage>) vaadinSession.getAttribute(UiStorage.class.getName());
        if (map == null) {
            map = new ConcurrentHashMap<>();
            vaadinSession.setAttribute(UiStorage.class.getName(), map);
        }
        return map;
    }
}
