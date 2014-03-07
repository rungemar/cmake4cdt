package com.rohde_schwarz.cmake4cdt.ui;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

import com.rohde_schwarz.cmake4cdt.Activator;


public class TargetArchInitializer implements IValueVariableInitializer {
	@Override
	public void initialize(IValueVariable variable) {
		String curArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		variable.setValue(curArch);

	}

}
