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
package org.eclipse.cdt.cmake.ui;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_AVAIL_TARGET_ARCHS, "arm;ppc;x86;native");
		store.setDefault(PreferenceConstants.P_AVAIL_TARGET_DEVICES, "localhost;rasPi");
		store.setDefault(PreferenceConstants.P_NOT_INST_SPECIFIC, "");

		store.setDefault(PreferenceConstants.P_BUILDDIR, "${CMake_ProjectPath}/${ConfigName}");
		store.setDefault(PreferenceConstants.P_DESTDIR, "${env_var:HOME}/${currentTargetDevice}/${ConfigName}/opt/");
	}

}
