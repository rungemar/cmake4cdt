package com.rohde_schwarz.cmake4cdt.ui;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

import com.rohde_schwarz.cmake4cdt.Activator;
import com.rohde_schwarz.cmake4cdt.ui.PreferenceConstants;


public class TargetDeviceInitializer implements IValueVariableInitializer {

	@Override
	public void initialize(IValueVariable variable) {
		String curDevice = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);
		variable.setValue(curDevice);
	}

}
