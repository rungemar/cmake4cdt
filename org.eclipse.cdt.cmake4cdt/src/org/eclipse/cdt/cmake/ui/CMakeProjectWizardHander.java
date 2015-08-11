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

import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSWizardHandler;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;


/**
 * @author runge_m
 *
 */
public class CMakeProjectWizardHander extends MBSWizardHandler {

	/**
	 * @param val
	 * @param p
	 * @param w
	 */
	public CMakeProjectWizardHander(IBuildPropertyValue val, Composite p, IWizard w) {
		super(val, p, w);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pt
	 * @param parent
	 * @param wizard
	 */
	public CMakeProjectWizardHander(IProjectType pt, Composite parent, IWizard wizard) {
		super(pt, parent, wizard);
		// TODO Auto-generated constructor stub
	}

}
