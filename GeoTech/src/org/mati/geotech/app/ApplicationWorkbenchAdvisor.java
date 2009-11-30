package org.mati.geotech.app;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    @Override
    public String getInitialWindowPerspectiveId() {
        return PerspectiveFactory.ID;
    }
    
    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer) {
        
        configurer.setShowCoolBar(false);
        
        return super.createWorkbenchWindowAdvisor(configurer);
    }

}
