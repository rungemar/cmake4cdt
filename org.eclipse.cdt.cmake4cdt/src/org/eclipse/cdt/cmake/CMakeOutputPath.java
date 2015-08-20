/*******************************************************************************
 * Copyright (c) 2014, 2015 Rohde & Schwarz GmbH & Co. KG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Runge - initial implementation of cmake support
 *******************************************************************************/
package org.eclipse.cdt.cmake;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.cdt.cmake.ui.CMakePropertyConstants;
import org.eclipse.cdt.cmake.ui.PreferenceConstants;

public class CMakeOutputPath {

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator#getBuildWorkingDir()
	 */
	static public IPath getPath(IProject project, String configName ) {

		IEclipsePreferences projectProperties = new ProjectScope(project).getNode("org.eclipse.cdt.cmake.scope"); //$NON-NLS-1$
		boolean buildDirWorkspaceSettings = true;
		if (projectProperties != null) {
			buildDirWorkspaceSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);
		}

		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();

		// evil hack: CMake_ProjectPath is just a replacement for ${project_loc}, because ${project_loc} does not seem to be updated 
		// when selecting multiple projects in project explorer and running CMake or building for all of them. 
		// make sure, that current project location is stored in ${CMake_ProjectPath}
		IPath projDir = project.getLocation();
		IValueVariable cmakeProjectDirVar = varMgr.getValueVariable("CMake_ProjectPath"); //$NON-NLS-1$
		if(cmakeProjectDirVar == null) {
			IValueVariable cnVar = varMgr.newValueVariable("CMake_ProjectPath", "Dummy variable to have a variable that holds the current project's name."); //$NON-NLS-1$
			try {
				varMgr.addVariables( new IValueVariable[]{cnVar} );
				cmakeProjectDirVar = varMgr.getValueVariable("CMake_ProjectPath"); //$NON-NLS-1$
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}      
		cmakeProjectDirVar.setValue(projDir.toString());

		String buildDirSetting = ""; //$NON-NLS-1$
		if(buildDirWorkspaceSettings) {
			String strWithVars = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);

			IValueVariable configNameVar = varMgr.getValueVariable("ConfigName"); //$NON-NLS-1$

			// evil hack: ConfigName should be available as Variable
			if(configNameVar == null) {
				IValueVariable cnVar = varMgr.newValueVariable("ConfigName", "Dummy variable to have a variable that holds the current configururation for use in build working dir."); //$NON-NLS-1$ //$NON-NLS-2$
				try {
					varMgr.addVariables( new IValueVariable[]{cnVar} );
					configNameVar = varMgr.getValueVariable("ConfigName"); //$NON-NLS-1$
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}      
			configNameVar.setValue( configName );

			try {
				buildDirSetting = varMgr.performStringSubstitution(strWithVars);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			String strWithVars = projectProperties.get(CMakePropertyConstants. P_BUILD_PATH, ""); //$NON-NLS-1$

			try {
				buildDirSetting = varMgr.performStringSubstitution(strWithVars);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		IPath buildDir = new Path(buildDirSetting);
		return buildDir;
	}
}



