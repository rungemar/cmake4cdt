package com.rohde_schwarz.cmake4cdt.ui;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

import com.rohde_schwarz.cmake4cdt.Activator;
import com.rohde_schwarz.cmake4cdt.ui.PreferenceConstants;

public class DestdirInitializer implements IValueVariableInitializer {

	@Override
	public void initialize(IValueVariable variable) {
		String destdirPref = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_DESTDIR);
		variable.setValue(destdirPref);
	}

}
