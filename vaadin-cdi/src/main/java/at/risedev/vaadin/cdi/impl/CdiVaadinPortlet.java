package at.risedev.vaadin.cdi.impl;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinPortletService;

public class CdiVaadinPortlet extends VaadinPortlet {

    @Override
    protected VaadinPortletService createPortletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        VaadinPortletService service = super.createPortletService(deploymentConfiguration);
        service.addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                event.getSession().addUIProvider(new CdiUIProvider());
            }
        });
        return service;
    }
}
