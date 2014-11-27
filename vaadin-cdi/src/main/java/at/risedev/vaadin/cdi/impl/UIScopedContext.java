package at.risedev.vaadin.cdi.impl;

import at.risedev.vaadin.cdi.api.UIScoped;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.annotation.Annotation;

public class UIScopedContext extends AbstractContext {

    private static final Logger logger = LoggerFactory.getLogger(UIScopedContext.class);

    // this will get created lazily
    private volatile UiStorage _uiStorage;

    private BeanManager beanManager;

    public UIScopedContext(BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    protected ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        Integer uiId = getCurrentUiId();
        if (uiId == null) {
            throw new IllegalStateException("UIScoped is used outside of ui lifecycle");
        }

        ContextualStorage contextualStorage = getUiStorage().get(uiId);

        if (createIfNotExist && contextualStorage == null) {
            contextualStorage = new ContextualStorage(beanManager, true, true);
            ContextualStorage oldStorage = getUiStorage().put(uiId, contextualStorage);
            if (oldStorage != null) {
                contextualStorage = oldStorage;
            }
        }

        return contextualStorage;
    }

    private Integer getCurrentUiId() {
        UI currentUI = CurrentInstance.get(UI.class);
        if (currentUI != null) {
            return currentUI.getUIId();
        }
        CurrentUiId currentUiId = CurrentInstance.get(CurrentUiId.class);
        return currentUiId != null ? currentUiId.getUiId() : null;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return UIScoped.class;
    }

    @Override
    public boolean isActive() {
        return UI.getCurrent() != null || getCurrentUiId() != null;
    }

    /**
     * destroy all Contextual Instances of the given uiId
     * @param uiId id of the UI to destroy
     */
    public void cleanupUi(Integer uiId) {
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        if (vaadinSession != null && vaadinSession.getState() == VaadinSession.State.OPEN) {
            UiStorage uiStorage = getUiStorage();
            ContextualStorage contextualStorage = uiStorage.get(uiId);
            if (contextualStorage != null) {
                destroyAllActive(contextualStorage);
                uiStorage.getContextualStorageMap().remove(uiId);
                logger.info("removing timed out Vaadin uiId {} from UiStorage", uiId);
            }
        }
    }

    private UiStorage getUiStorage() {
        if (_uiStorage == null) {
            _uiStorage = BeanProvider.getContextualReference(UiStorage.class);
        }
        return _uiStorage;
    }


}