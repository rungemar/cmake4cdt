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
package org.eclipse.cdt.cmake;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.cmake.ui.CMakePropertyConstants;
import org.eclipse.cdt.cmake.ui.PreferenceConstants;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.cdt.core.IConsoleParser;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.resources.ACBuilder;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.internal.core.BuildRunnerHelper;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedBuilderCorePlugin;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

public class CMakeProjectBuilder extends ACBuilder {

	public static String BUILDER_ID="org.eclipse.cdt.cmake.CMakeProjectBuilder";
	public final String CMAKE_EXE = "cmake";
	
	private IProject privateProject = null;
	private IManagedBuildInfo privateBuildInfo = null;
		
	public CMakeProjectBuilder() {
		super();
	}
	
	public CMakeProjectBuilder( IProject project, IManagedBuildInfo info ) {
		super();
		privateProject = project;
		privateBuildInfo = info;
	}

	@Override
	public IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {

		IProject project;
		IManagedBuildInfo buildInfo;
		if(privateProject == null) {
			project = getProject();
		} else {
			project = privateProject;
		}
		
		if(privateBuildInfo == null) {
			buildInfo = ManagedBuildManager.getBuildInfo(project);
		} else {
			buildInfo = privateBuildInfo;
		}
		 
		IConfiguration activeConfig = buildInfo.getDefaultConfiguration();
		
		MultiStatus mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", 0, "success", null );

	
		String currentConf = activeConfig.getName();
		String currentArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		String currentInstrument = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);


		IPath buildDir = getBuildWorkingDir(currentConf, currentArch);

		IConsole cmakeConsole = CCorePlugin.getDefault().getConsole("org.eclipse.cdt.cmake.ui.CMakeConsole"); //$NON-NLS-1$
		cmakeConsole.start(project);
		
		@SuppressWarnings("restriction")
		BuildRunnerHelper buildRunnerHelper = new BuildRunnerHelper(project);
		ICommandLauncher launcher = new CommandLauncherRC();
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();

		try {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			monitor.beginTask("Invoking CMake for '" + project.getName() + "'", 100);//$NON-NLS-1$ //$NON-NLS-2$
			
			
			IEclipsePreferences projectProperties = new ProjectScope(project).getNode("org.eclipse.cdt.cmake.scope"); //$NON-NLS-1$
			boolean deviceSpecific = false;
			boolean useWorkspaceBuidDirSettings = true;
			if (projectProperties != null) {
				deviceSpecific = projectProperties.getBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, false);
				useWorkspaceBuidDirSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);
			}
			
			if(!buildDir.toFile().exists()) {
				boolean retval = buildDir.toFile().mkdir();
				if(retval == false) {
					String msg = new String("Could not create build output dir: '" + buildDir.toOSString() + "'");
					throw new CoreException(new Status(IStatus.ERROR, ManagedBuilderCorePlugin.PLUGIN_ID, msg, new Exception()));
				}
			}
			

			IValueVariable destdirVar = varMgr.getValueVariable("CMake_DESTDIR"); //$NON-NLS-1$
			String destDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_DESTDIR);
			destDirStr = varMgr.performStringSubstitution(destDirStr);
			destdirVar.setValue( destDirStr );

			// buildDir = varMgr.performStringSubstitution(buildDir);
			String currentToolchainFile;
			IDynamicVariable toolchainFileVar = varMgr.getDynamicVariable("org.eclipse.cdt.cmake.var.toolchainFile"); //$NON-NLS-1$

			currentToolchainFile = toolchainFileVar.getValue(currentArch);

			List<String> cmakeArgs = new ArrayList<String>();
			
			cmakeArgs.add("-DCMAKE_BUILD_TYPE=" + currentConf);
			cmakeArgs.add("-DCMAKE_TOOLCHAIN_FILE=" + currentToolchainFile);
			cmakeArgs.add("-DCMAKE_EXPORT_COMPILE_COMMANDS=On");
			cmakeArgs.add( project.getLocation().toString() );
			

			// add cmake extra FLAGS
			
			// add some cmake options
			

			IContributedEnvironment ice = CCorePlugin.getDefault().getBuildEnvironmentManager().getContributedEnvironment();
			ICConfigurationDescription cfgDesc = CoreModel.getDefault().getProjectDescription(project).getConfigurationByName(currentConf);
			IEnvironmentVariable[] envvars = ice.getVariables( cfgDesc );
			
			String[] envp = new String[envvars.length];
			for(int i = 0; i < envvars.length; i++ ) {
				envp[i] = envvars[i].getName() + "=" + envvars[i].getValue(); //$NON-NLS-1$
			}
			
			File buildDirFile = buildDir.toFile();
			if(buildDirFile.exists()) {
				if(!buildDirFile.isDirectory()) {
					throw new RuntimeException( "The path that is to be used for the build: '" + buildDir + "' exists, but is no directory."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {
				boolean success = buildDirFile.mkdirs();
				if(success = false) {
					throw new RuntimeException("Could not create build dir: '" + buildDir + "'.");
				}
			}
			
			URI workingDirectoryURI = new URI("file://" + buildDir); //$NON-NLS-1$
			
			IPath cmakePath = new Path(CMAKE_EXE);
			
			String[] a = new String[cmakeArgs.size()];
			buildRunnerHelper.setLaunchParameters(launcher, cmakePath, cmakeArgs.toArray(a), workingDirectoryURI, null);
			
			ErrorParserManager epm = new ErrorParserManager(project, workingDirectoryURI, null, null);
			List<IConsoleParser> consoleParsers = new ArrayList<IConsoleParser>(); 
			buildRunnerHelper.prepareStreams(epm, consoleParsers, (org.eclipse.cdt.core.resources.IConsole) cmakeConsole, new SubProgressMonitor(monitor, 20));

			buildRunnerHelper.removeOldMarkers(project, new SubProgressMonitor(monitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			buildRunnerHelper.greeting(IncrementalProjectBuilder.FULL_BUILD, currentConf, "Running CMake ...", true); //$NON-NLS-1$
			int state = buildRunnerHelper.build(new SubProgressMonitor(monitor, 60, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			buildRunnerHelper.close();
			buildRunnerHelper.goodbye();

			if (state != ICommandLauncher.ILLEGAL_COMMAND) {
				if(state == ICommandLauncher.OK) {
					buildRunnerHelper.refreshProject(currentConf, new SubProgressMonitor(monitor, 90, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
				}
				else {
					mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", IStatus.ERROR, "CMake failed to generate Makefile. Please see CMake Console for Details.", null );
				}
			}
			else {
				String msg = ""; //$NON-NLS-1$
				throw new CoreException(new Status(IStatus.ERROR, ManagedBuilderCorePlugin.PLUGIN_ID, msg, new Exception()));
			}
		} catch (CoreException ce) {
			mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", -1, ce.getStatus().getMessage(), null );
			logToConsole(cmakeConsole, ce.getStatus());
			throw new OperationCanceledException(ce.getStatus().getMessage());
		} 
		catch (Exception e) {
			String msg = "Error running CMake for project: '" + project.getName() +"' in configuration '" + currentConf + "'.";
			throw new CoreException(new Status(IStatus.ERROR, ManagedBuilderCorePlugin.PLUGIN_ID, msg, e));
		} 
		finally {
			try {
				buildRunnerHelper.close();
			} catch (IOException e) {
				ManagedBuilderCorePlugin.log(e);
			}
			monitor.done();
		}
		
		return project.getReferencedProjects();
	}
	
	private IPath getBuildWorkingDir(String currentConf, String currentArch) {
		
		IEclipsePreferences projectProperties = new ProjectScope(getProject()).getNode("org.eclipse.cdt.cmake.scope"); //$NON-NLS-1$
		boolean buildDirWorkspaceSettings = true;
		if (projectProperties != null) {
			buildDirWorkspaceSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);
		}
		
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		
		// make sure, that current project location is stored in ${BuildIF_ProjectPath}
		IPath projDir = getProject().getLocation();
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
			
			configNameVar.setValue( currentConf );
			
			try {
				buildDirSetting = varMgr.performStringSubstitution(strWithVars);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			String strWithVars = projectProperties.get(CMakePropertyConstants.P_BUILD_PATH, ""); //$NON-NLS-1$

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
	
	private void logToConsole(IConsole console, IStatus status) {
		String errmsg = new String();
		OutputStream cos;
		try {
			cos = console.getOutputStream();
			StringBuffer buf = new StringBuffer(errmsg);
			buf.append(System.getProperty("line.separator", "\n")); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append(status.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$

			try {
				cos.write(buf.toString().getBytes());
				cos.flush();
				cos.close();
			} catch (IOException e) {
				ResourcesPlugin.getPlugin().getLog().log(status);
			}

		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
}
