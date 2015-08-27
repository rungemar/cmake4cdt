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


import java.io.File;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CMakePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private BooleanFieldEditor cmakeViaPathEditor = null;
	private DestdirFieldEditor cmakePathEditor = null;
	
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
		
		
		cmakeViaPathEditor = new BooleanFieldEditor( PreferenceConstants.P_CMAKE_VIA_PATH, "Find cmake via &Path", getFieldEditorParent()) {
			public void valueChanged(boolean oldValue, boolean newValue)
			{
				adjustVisibility(newValue);
				validateForm();
            	super.valueChanged(oldValue, newValue);
			}
		};
		
		addField(cmakeViaPathEditor);
		
		cmakePathEditor = new DestdirFieldEditor( PreferenceConstants.P_CMAKE_EXE, "&CMake installation dir:", getFieldEditorParent()) {
			public void valueChanged() {
				validateForm();
				super.valueChanged();
			}

		}; 
		
		addField( cmakePathEditor );
		addField( new DestdirFieldEditor( PreferenceConstants.P_BUILDDIR, "&Build in dir:", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_DESTDIR, "&DESTDIR:", getFieldEditorParent()));
		
		adjustVisibility(getPreferenceStore().getBoolean(PreferenceConstants.P_CMAKE_VIA_PATH));
	}

	/**
	 * 
	 */
	protected void adjustVisibility(boolean newValue) {
    	if(newValue == true) {
    		cmakePathEditor.setEnabled(false, getFieldEditorParent());
    	}
    	else {
    		cmakePathEditor.setEnabled(true, getFieldEditorParent());
    	}
	}

	private void validateForm() {
		boolean isValid = false;

		boolean cmakeViaPath = cmakeViaPathEditor.getBooleanValue();
		if(cmakeViaPath) {
			isValid = true;
		}
		else {
			String currentCMakePath = cmakePathEditor.getStringValue();
			File f = new File(currentCMakePath);
			
			if(f.canExecute()) {
				isValid = true;
				/// TODO: add more tests here
			}
		}
		setValid(isValid);
	}
}
