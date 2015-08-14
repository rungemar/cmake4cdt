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
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CMakePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private FieldEditor targetDevicesEditor;

	public CMakePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Workspace wide settings for CMake. These values can be overridden per project in the project properties.\n");
	}

	@Override
	public void init(IWorkbench workbench) {
	}


	@Override
	protected void createFieldEditors() {
		//addField( new ArchTable(PreferenceConstants.P_AVAIL_TARGET_ARCHS, "Available Architectures: ", getFieldEditorParent()));
		//addField( new TargetDevicesEditor(PreferenceConstants.P_AVAIL_TARGET_DEVICES, "target devices: ", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_BUILDDIR, "&Build in dir:", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_DESTDIR, "&DESTDIR:", getFieldEditorParent()));
	}

}
