package com.rohde_schwarz.cmake4cdt.ui;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.rohde_schwarz.cmake4cdt.ui.AvailArchsEditor;
import com.rohde_schwarz.cmake4cdt.ui.TargetDevicesEditor;
import com.rohde_schwarz.cmake4cdt.ui.DestdirFieldEditor;
import com.rohde_schwarz.cmake4cdt.ui.PreferenceConstants;
import com.rohde_schwarz.cmake4cdt.Activator;

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
		addField( new AvailArchsEditor(PreferenceConstants.P_AVAIL_TARGET_ARCHS, "Available Architectures: ", getFieldEditorParent()));
		addField( new TargetDevicesEditor(PreferenceConstants.P_AVAIL_TARGET_DEVICES, "target devices: ", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_BUILDDIR, "&Build in dir:", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_DESTDIR, "&DESTDIR:", getFieldEditorParent()));
	}

}
