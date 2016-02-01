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
import org.eclipse.cdt.cmake.CMakeInfo;
import org.eclipse.cdt.cmake.CMakeInfoRetriever;
import org.eclipse.cdt.cmake.CMakeOutputPath;
import org.eclipse.cdt.cmake.IAcceptsCMakeInfo;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CMakePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, IAcceptsCMakeInfo {


	private BooleanFieldEditor cmakeViaPathEditor = null;
	private DestdirFieldEditor cmakePathEditor = null;

	private Label  cmakeVersionLabel2 = null;
	private ComboFieldEditor cmakeGeneratorsEditor = null;
	
	private CMakeInfoRetriever cmakeInfoRetriever;
	
	public CMakePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Workspace wide settings for CMake. These values can be overridden per project in the project properties.\n");
		
		cmakeInfoRetriever = new CMakeInfoRetriever(this);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		//TODO get project and build condif
		//CMakeOutputPath.getPath(project, configName);
		
		return super.performOk();
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
		
		Label cmakeVersionLabel1 = new Label(getFieldEditorParent(), SWT.NONE);
		GridData gd = new GridData();
		cmakeVersionLabel1.setLayoutData(gd);
		cmakeVersionLabel1.setText("CMake version:");
		
		cmakeVersionLabel2 = new Label(getFieldEditorParent(), SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.horizontalSpan = cmakePathEditor.getNumberOfControls() - 1;
		cmakeVersionLabel2.setLayoutData(gd);
		cmakeVersionLabel2.setText("<CMake version>");

		cmakeGeneratorsEditor = new ComboFieldEditor(PreferenceConstants.P_CMAKE_GENERATOR, "Generator (-G):", CMakeInfo.getKnownGenerators(), getFieldEditorParent());
		
		addField(cmakeGeneratorsEditor);
		addField( new StringFieldEditor("addArgs", "Additional CMake arguments", getFieldEditorParent()) );

		addField( new DestdirFieldEditor( PreferenceConstants.P_BUILDDIR, "&Build in dir:", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_DESTDIR, "&DESTDIR:", getFieldEditorParent()));
	}

	protected void initialize() {
		super.initialize();

		adjustVisibility(getPreferenceStore().getBoolean(PreferenceConstants.P_CMAKE_VIA_PATH));
		validateForm();
	}

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
			String currentCMakePath = "cmake";
			IPath p = new Path(currentCMakePath);
			cmakeInfoRetriever.tryExecCMake(p);
		}
		else {
			String currentCMakePath = cmakePathEditor.getStringValue();
			IPath p = new Path(currentCMakePath);
			File f = p.toFile();
			if(f.canExecute()) {
				cmakeInfoRetriever.tryExecCMake(p);
			}
		}
		setValid(isValid);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.cmake.IAcceptsCMakeInfo#setCMakeInfo(org.eclipse.cdt.cmake.CMakeInfo)
	 */
	@Override
	public void setCMakeInfo(CMakeInfo info) {
		cmakeVersionLabel2.setText(info.getCMakeVersion() );
		String generators[] = info.keySet().toArray(new String[info.size()]);
	}
	
	
	
	
	
}
