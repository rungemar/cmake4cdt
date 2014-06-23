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
package org.eclipse.cdt.multiarch;

import org.eclipse.cdt.multiarch.Activator;
import org.eclipse.cdt.multiarch.PreferenceConstants;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;


public class TargetDeviceInitializer implements IValueVariableInitializer {

	@Override
	public void initialize(IValueVariable variable) {
		String curDevice = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);
		variable.setValue(curDevice);
	}

}
