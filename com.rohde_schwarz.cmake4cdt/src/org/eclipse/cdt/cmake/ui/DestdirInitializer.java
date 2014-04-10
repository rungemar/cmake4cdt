package org.eclipse.cdt.cmake.ui;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.ui.PreferenceConstants;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class DestdirInitializer implements IValueVariableInitializer {

	@Override
	public void initialize(IValueVariable variable) {
		String destdirPref = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_DESTDIR);
		variable.setValue(destdirPref);
	}

}
