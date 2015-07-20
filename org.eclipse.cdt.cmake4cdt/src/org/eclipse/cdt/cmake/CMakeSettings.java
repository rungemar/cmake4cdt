/*******************************************************************************
 * Copyright (c) 2014 Rohde & Schwarz GmbH & Co. KG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Runge - initial implementation of cmake support
 *******************************************************************************/

package org.eclipse.cdt.cmake;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class CMakeSettings implements PropertyChangeListener {

//	private String curTargetArch;
//	private String curTargetDevice;

//	private List<String> availTargetArchs; 
//	private List<String> availTargetDevices; 

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);	

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

//	public String getCurrentTargetArch() {
//		return curTargetArch;
//	}
//	
//	public void setCurrentTargetArch(String curTargetArch) {
//	    propertyChangeSupport.firePropertyChange("currentTargetArch", this.curTargetArch, this.curTargetArch = curTargetArch);  
//
//	    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_TARGET_ARCH, curTargetArch);
//
//		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
//		IValueVariable archVar = varMgr.getValueVariable("CMake_TargetArch");
//		archVar.setValue(curTargetArch);
//	}
//
//	public String getCurrentTargetDevice() {
//		return curTargetDevice;
//	}
//
//	public void setCurrentTargetDevice(String currentTargetDevice) {
//	    propertyChangeSupport.firePropertyChange("currentTargetDevice", this.curTargetDevice, this.curTargetDevice = currentTargetDevice );
//
//	    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_TARGET_DEVICE, curTargetDevice);
//		
//		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
//		IValueVariable targetVar = varMgr.getValueVariable("CMake_TargetDevice");
//		targetVar.setValue(curTargetDevice);
//	}
	
//	public List<String> getAvailTargetDevices() {
//		return availTargetDevices;
//	}
//
//	public void setAvailTargetDevices(List<String> availTargetDevices) {
//	    propertyChangeSupport.firePropertyChange("availTargetDevices", this.availTargetDevices, this.availTargetDevices = availTargetDevices );
//	}
//	
//	public List<String> getAvailTargetArchs() {
//		return availTargetArchs;
//	}
//
//	public void setAvailTargetArchs(List<String> availTargetArchs) {
//	    propertyChangeSupport.firePropertyChange("availTargetArchs", this.availTargetArchs, this.availTargetArchs = availTargetArchs );
//	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}

}
