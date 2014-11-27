package at.risedev.vaadin.cdi.impl;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.DefaultUIProvider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CdiUIProvider extends DefaultUIProvider {

    private VaadinUIExtension vaadinUIExtension;

    @Override
    public UI createInstance(UICreateEvent event) {

        try {
            final Integer uiId = event.getUiId();
            CurrentUiId currentUiId = new CurrentUiId(uiId);
            CurrentInstance.set(CurrentUiId.class, currentUiId);
            final UI ui = BeanProvider.getContextualReference(event.getUIClass());
            ui.addDetachListener(new ClientConnector.DetachListener() {
                @Override
                public void detach(ClientConnector.DetachEvent event) {
                    getUIScopedExtension().getContext().cleanupUi(uiId);
                }
            });
            return ui;
        } finally {
            CurrentInstance.set(CurrentUiId.class, null);
        }
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        String name = event.getService().getServiceName();
        if (getVaadinUIExtension().getUis().keySet().contains(name)) {
            return getVaadinUIExtension().getUis().get(name);
        }
        if (getVaadinUIExtension().getUis().containsKey("")) {
            return getVaadinUIExtension().getUis().get("");
        }
        return super.getUIClass(event);
    }

    private VaadinUIExtension getUIScopedExtension() {
        return BeanProvider.getContextualReference(VaadinUIExtension.class);
    }

    protected VaadinUIExtension getVaadinUIExtension() {
        if (vaadinUIExtension == null) {
            vaadinUIExtension = BeanProvider.getContextualReference(VaadinUIExtension.class);
        }
        return vaadinUIExtension;
    }
}
