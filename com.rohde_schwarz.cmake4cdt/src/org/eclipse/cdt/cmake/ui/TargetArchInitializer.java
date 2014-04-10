package org.eclipse.cdt.cmake.ui;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;


public class TargetArchInitializer implements IValueVariableInitializer {
	@Override
	public void initialize(IValueVariable variable) {
		String curArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		variable.setValue(curArch);

	}

}
