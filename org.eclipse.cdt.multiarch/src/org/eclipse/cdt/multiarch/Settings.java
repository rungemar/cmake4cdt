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
	
	private String curArch;
	private String curTargetDevice;

	private List<String> availArchs; 
	private List<String> availTargetDevices; 

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);	

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public String getCurArch() {
		return curArch;
	}
	
	public void setCurArch(String curArch) {
	    propertyChangeSupport.firePropertyChange("curArch", this.curArch, this.curArch = curArch);  

	    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_ARCH, curArch);

		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable archVar = varMgr.getValueVariable("BuildIF_Arch");
		archVar.setValue(curArch);
	}

	public String getCurTarget() {
		return curTargetDevice;
	}

	public void setCurTarget(String curTarget) {
	    propertyChangeSupport.firePropertyChange("curTargetDevice", this.curTargetDevice, this.curTargetDevice = curTarget );

	    Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_INSTRUMENT, curTarget);
		
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable archVar = varMgr.getValueVariable("BuildIF_Instrument");
		archVar.setValue(curTarget);
	}
	
	public List<String> getAvailTargets() {
		return availTargetDevices;
	}

	public void setAvailTargets(List<String> availTargets) {
	    propertyChangeSupport.firePropertyChange("availTargetDevices", this.availTargetDevices, this.availTargetDevices = availTargets );
	}
	
	public List<String> getAvailArchs() {
		return availArchs;
	}

	public void setAvailArchs(List<String> availArchs) {
	    propertyChangeSupport.firePropertyChange("availArchs", this.availArchs, this.availArchs = availArchs );
	}


	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
	}

}
