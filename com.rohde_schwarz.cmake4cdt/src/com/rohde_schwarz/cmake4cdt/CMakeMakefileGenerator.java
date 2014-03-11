package com.rohde_schwarz.cmake4cdt;

import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

import com.rohde_schwarz.cmake4cdt.Activator;
import com.rohde_schwarz.cmake4cdt.ui.CMakePropertyConstants;
import com.rohde_schwarz.cmake4cdt.ui.CMakePropertyConstants;
import com.rohde_schwarz.cmake4cdt.ui.PreferenceConstants;

public class CMakeMakefileGenerator implements IManagedBuilderMakefileGenerator {

	Path workspacePath;
	String projectName;
	IManagedBuildInfo buildInfo;
	IProject project;

	@Override
	public void generateDependencies() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiStatus generateMakefiles(IResourceDelta delta)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getBuildWorkingDir() {
		IEclipsePreferences projectProperties = new ProjectScope(project).getNode("com.rohde_schwarz.cmake4cdt.scope");
		boolean buildDirWorkspaceSettings = true;
		if (projectProperties != null) {
			buildDirWorkspaceSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);
		}
		
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		
		// make sure, that current project location is stored in ${BuildIF_ProjectPath}
		IPath projDir = project.getLocation();
		IValueVariable buildifProjectDirVar = varMgr.getValueVariable("BuildIF_ProjectPath");
		buildifProjectDirVar.setValue(projDir.toString());

		String buildDirSetting = "";
		if(buildDirWorkspaceSettings) {
			String strWithVars = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);
			
			IValueVariable configNameVar = varMgr.getValueVariable("ConfigName");
			
			// evil hack: ConfigName should be available as Variable
			if(configNameVar == null) {
				 IValueVariable cnVar = varMgr.newValueVariable("ConfigName", "Dummy variable to have a variable that holds the current configururation for use in build working dir.");
			    try {
					varMgr.addVariables( new IValueVariable[]{cnVar} );
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			configNameVar = varMgr.getValueVariable("ConfigName");
			configNameVar.setValue( buildInfo.getConfigurationName() );
			
			try {
				buildDirSetting = varMgr.performStringSubstitution(strWithVars);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			String strWithVars = projectProperties.get(CMakePropertyConstants. P_BUILD_PATH, "");

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

	@Override
	public String getMakefileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IProject project, IManagedBuildInfo info,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isGeneratedResource(IResource resource) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void regenerateDependencies(boolean force) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiStatus regenerateMakefiles() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
