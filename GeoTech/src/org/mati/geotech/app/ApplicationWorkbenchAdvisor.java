package org.mati.geotech.app;

import org.eclipse.ui.application.WorkbenchAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    @Override
    public String getInitialWindowPerspectiveId() {
        return PerspectiveFactory.ID;
    }

}
