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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.ui.CMakePropertyConstants;
import org.eclipse.cdt.cmake.ui.PreferenceConstants;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.cdt.core.IConsoleParser;
import org.eclipse.cdt.core.envvar.IContributedEnvironment;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.internal.core.BuildRunnerHelper;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedBuilderCorePlugin;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

public class CMakeMakefileGenerator implements IManagedBuilderMakefileGenerator {

	Path workspacePath;
	String projectName;
	IManagedBuildInfo buildInfo;
	IProject project;
	IProgressMonitor monitor;
	
	public final String CMAKE_EXE = "cmake";

	@Override
	public void generateDependencies() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiStatus generateMakefiles(IResourceDelta delta)
			throws CoreException {
//		MultiStatus mstatus = regenerateMakefiles();
		MultiStatus mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", 0, "success", null );
		return  mstatus;
	}

	@Override
	public IPath getBuildWorkingDir() {
		return CMakeOutputPath.getPath(project, buildInfo.getConfigurationName());
	}

	@Override
	public String getMakefileName() {
		// @TODO get settings for selected CMake generator and adjust Makefile Name (e.g. in case of ninja make)
		return "Makefile";
	}

	@Override
	public void initialize(IProject project, IManagedBuildInfo info,
			IProgressMonitor monitor) {
		this.project = project;
		this.buildInfo = info; 
		this.monitor = monitor;
	}

	@Override
	public boolean isGeneratedResource(IResource resource) {
		return true;
	}

	@Override
	public void regenerateDependencies(boolean force) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiStatus regenerateMakefiles() throws CoreException {
		MultiStatus mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", 0, "success", null );
		return mstatus;
	}
	
	@SuppressWarnings("restriction")
	public MultiStatus runCMake() throws CoreException {
		MultiStatus mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", 0, "success", null );
		
		String currentConf = buildInfo.getConfigurationName();
		String currentArch =  Platform.getPreferencesService().getString("org.eclipse.cdt.multiarch", PreferenceConstants.P_CURRENT_TARGET_ARCH, "native", null); 
		//String currentArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		String currentInstrument = Platform.getPreferencesService().getString("org.eclipse.cdt.multiarch", PreferenceConstants.P_CURRENT_TARGET_DEVICE, "host", null);
		// String currentInstrument = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);

		IConsole cmakeConsole = CCorePlugin.getDefault().getConsole("org.eclipse.cdt.cmake.ui.CMakeConsole"); //$NON-NLS-1$
		cmakeConsole.start(project);
		
		@SuppressWarnings("restriction")
		BuildRunnerHelper buildRunnerHelper = new BuildRunnerHelper(this.project);
		ICommandLauncher launcher = new CommandLauncherRC();
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();

		try {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			monitor.beginTask("Invoking CMake for '" + project.getName() + "'", 100);//$NON-NLS-1$ //$NON-NLS-2$
			
			IPath buildDir = null;
			
			IEclipsePreferences projectProperties = new ProjectScope(project).getNode("org.eclipse.cdt.cmake.scope"); //$NON-NLS-1$
			boolean deviceSpecific = false;
			boolean useWorkspaceBuidDirSettings = true;
			if (projectProperties != null) {
				deviceSpecific = projectProperties.getBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, false);
				useWorkspaceBuidDirSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);
			}
			
			buildDir = getBuildWorkingDir();
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
			if(!currentToolchainFile.equals("<none>")) {
				cmakeArgs.add("-DCMAKE_TOOLCHAIN_FILE=" + currentToolchainFile);
			}
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
			logToConsole(cmakeConsole, ce.getStatus());
			mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", -1, ce.getStatus().getMessage(), null );
		} 
		catch (Exception e) {
			String msg = "Error running CMake for project: '" + project.getName() +"' in configuration '" + currentConf + "'.";
			IStatus status = new Status(IStatus.ERROR, ManagedBuilderCorePlugin.PLUGIN_ID, msg, e);
			logToConsole(cmakeConsole, status);
			throw new CoreException(status);
		} 
		finally {
			try {
				buildRunnerHelper.close();
			} catch (IOException e) {
				ManagedBuilderCorePlugin.log(e);
			}
			monitor.done();
		}
		
		return mstatus;
	}
	
	public void logToConsole(IConsole console, IStatus status) {
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

 
	 private String inputStream2String(InputStream is) {
		 /*
		  * To convert the InputStream to String we use the BufferedReader.readLine()
		  * method. We iterate until the BufferedReader return null which means
		  * there's no more data to read. Each line will appended to a StringBuilder
		  * and returned as String.
		  */
		 BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		 StringBuilder sb = new StringBuilder();
		  
		 String line = null;
		 try {
			 while ((line = reader.readLine()) != null) {
				 sb.append(line + "\n"); //$NON-NLS-1$
			 }
		 } catch (IOException e) {
			 e.printStackTrace();
		 } finally {
			 try {
				 is.close();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 return sb.toString();
	 }

}
