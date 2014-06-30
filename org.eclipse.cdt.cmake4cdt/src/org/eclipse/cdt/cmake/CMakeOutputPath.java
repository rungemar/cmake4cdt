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

		// make sure, that current project location is stored in ${BuildIF_ProjectPath}
		IPath projDir = project.getLocation();
		IValueVariable cmakeProjectDirVar = varMgr.getValueVariable("CMake_ProjectPath"); //$NON-NLS-1$
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
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}      

			configNameVar = varMgr.getValueVariable("ConfigName"); //$NON-NLS-1$
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



