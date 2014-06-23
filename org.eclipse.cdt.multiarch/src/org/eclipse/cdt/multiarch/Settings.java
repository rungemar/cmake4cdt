package org.eclipse.cdt.multiarch;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
 

public class Settings implements PropertyChangeListener {
	
	private String currentTargetArch;
	private String currentTargetDevice;

	private List<String> availTargetArchs; 
	private List<String> availTargetDevices; 

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);	

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public String getCurrentTargetArch() {
		return currentTargetArch;
	}
	
	public void setCurrentTargetArch(String curArch) {
	    propertyChangeSupport.firePropertyChange(PreferenceConstants.P_CURRENT_TARGET_ARCH, this.currentTargetArch, this.currentTargetArch = curArch);  

	    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_TARGET_ARCH, curArch);

		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable archVar = varMgr.getValueVariable(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		archVar.setValue(curArch);
	}

	public String getCurrentTargetDevice() {
		return currentTargetDevice;
	}

	public void setCurrentTargetDevice(String curTarget) {
	    propertyChangeSupport.firePropertyChange(PreferenceConstants.P_CURRENT_TARGET_DEVICE, this.currentTargetDevice, this.currentTargetDevice = curTarget );

	    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_TARGET_DEVICE, curTarget);
		
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable archVar = varMgr.getValueVariable(PreferenceConstants.P_CURRENT_TARGET_DEVICE);
		archVar.setValue(curTarget);
	}
	
	public List<String> getAvailTargetsDevices() {
		return availTargetDevices;
	}

	public void setAvailTargetDevices(List<String> availTargets) {
	    propertyChangeSupport.firePropertyChange(PreferenceConstants.P_AVAIL_TARGET_DEVICES, this.availTargetDevices, this.availTargetDevices = availTargets );
	}
	
	public List<String> getAvailTargetArchs() {
		return availTargetArchs;
	}

	public void setAvailTargetArchs(List<String> availArchs) {
	    propertyChangeSupport.firePropertyChange(PreferenceConstants.P_AVAIL_TARGET_ARCHS, this.availTargetArchs, this.availTargetArchs = availArchs );
	}


	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
	}

}
