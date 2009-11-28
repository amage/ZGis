package org.mati.geotech.gui.states;

public interface GTMFStateChangeListener {
	public void stateChenged(GTMFState aState);
	public void createPntObjectReq();
	public void createLinePntReq();
	public void removeLinePntReq();
	public void createLineObj();
}
