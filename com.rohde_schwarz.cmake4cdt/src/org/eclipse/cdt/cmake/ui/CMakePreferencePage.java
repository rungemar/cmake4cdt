package org.eclipse.cdt.cmake.ui;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.ui.AvailArchsEditor;
import org.eclipse.cdt.cmake.ui.DestdirFieldEditor;
import org.eclipse.cdt.cmake.ui.PreferenceConstants;
import org.eclipse.cdt.cmake.ui.TargetDevicesEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
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
		addField( new AvailArchsEditor(PreferenceConstants.P_AVAIL_TARGET_ARCHS, "Available Architectures: ", getFieldEditorParent()));
		addField( new TargetDevicesEditor(PreferenceConstants.P_AVAIL_TARGET_DEVICES, "target devices: ", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_BUILDDIR, "&Build in dir:", getFieldEditorParent()));
		addField( new DestdirFieldEditor( PreferenceConstants.P_DESTDIR, "&DESTDIR:", getFieldEditorParent()));
	}

}
