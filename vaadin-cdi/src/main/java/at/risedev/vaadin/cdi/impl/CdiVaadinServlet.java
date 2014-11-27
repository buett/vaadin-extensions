package at.risedev.vaadin.cdi.impl;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;

public class CdiVaadinServlet extends VaadinServlet {

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        VaadinServletService servletService = super.createServletService(deploymentConfiguration);
        servletService.addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                event.getSession().addUIProvider(new CdiUIProvider());
            }
        });
        return servletService;
    }
}
