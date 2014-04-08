package com.rohde_schwarz.cmake4cdt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.cdt.internal.core.BuildRunnerHelper;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.core.ManagedBuilderCorePlugin;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedMakeMessages;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.rohde_schwarz.cmake4cdt.Activator;
import com.rohde_schwarz.cmake4cdt.ui.CMakePropertyConstants;
import com.rohde_schwarz.cmake4cdt.ui.PreferenceConstants;

public class CMakeMakefileGenerator implements IManagedBuilderMakefileGenerator {

	Path workspacePath;
	String projectName;
	IManagedBuildInfo buildInfo;
	IProject project;
	IProgressMonitor monitor;
	
	public final String CMAKE_EXE = ManagedMakeMessages.getString("CMakeMakefileGenerator.0"); //$NON-NLS-1$

	@Override
	public void generateDependencies() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public MultiStatus generateMakefiles(IResourceDelta delta)
			throws CoreException {
		MultiStatus mstatus = regenerateMakefiles();		
		return  mstatus;
	}

	@Override
	public IPath getBuildWorkingDir() {
		IEclipsePreferences projectProperties = new ProjectScope(project).getNode("com.rohde_schwarz.cmake4cdt.scope"); //$NON-NLS-1$
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
			configNameVar.setValue( buildInfo.getConfigurationName() );
			
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

	@Override
	public String getMakefileName() {
		// @TODO get settings for selected CMake generator and adjust Makefile Name (e.g. in case of ninja make)
		return ManagedMakeMessages.getString("CMakeMakefileGenerator.9"); //$NON-NLS-1$
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

	@SuppressWarnings("restriction")
	@Override
	public MultiStatus regenerateMakefiles() throws CoreException {
		MultiStatus mstatus = new MultiStatus(ManagedMakeMessages.getString("CMakeMakefileGenerator.10"), 0, ManagedMakeMessages.getString("CMakeMakefileGenerator.11"), null ); //$NON-NLS-1$ //$NON-NLS-2$

		String currentConf = buildInfo.getConfigurationName();
		String currentArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		String currentInstrument = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);

		IConsole cmakeConsole = CCorePlugin.getDefault().getConsole("com.rohde_schwarz.cmake4cdt.CMakeConsole"); //$NON-NLS-1$
		cmakeConsole.start(project);
		
		@SuppressWarnings("restriction")
		BuildRunnerHelper buildRunnerHelper = new BuildRunnerHelper(this.project);
		ICommandLauncher launcher = new CommandLauncher();
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();

		try {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			monitor.beginTask("Invoking CMake for '" + project.getName() + "'", 100);//$NON-NLS-1$ //$NON-NLS-2$
			
			
			String buildDir = null;
			
			IEclipsePreferences projectProperties = new ProjectScope(project).getNode("com.rohde_schwarz.cmake4cdt.scope"); //$NON-NLS-1$
			boolean deviceSpecific = false;
			boolean useWorkspaceBuidDirSettings = true;
			if (projectProperties != null) {
				deviceSpecific = projectProperties.getBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, false);
				useWorkspaceBuidDirSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);

				buildDir = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);

				if(!useWorkspaceBuidDirSettings) {
					buildDir = projectProperties.get( CMakePropertyConstants.P_BUILD_PATH, buildDir);
				}
				
			}

			IValueVariable destdirVar = varMgr.getValueVariable("CMake_DESTDIR"); //$NON-NLS-1$
			String destDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_DESTDIR);
			destDirStr = varMgr.performStringSubstitution(destDirStr);
			destdirVar.setValue( destDirStr );

			buildDir = varMgr.performStringSubstitution(buildDir);

			List<String> cmakeArgs = new ArrayList<String>();
			
			cmakeArgs.add(ManagedMakeMessages.getString("CMakeMakefileGenerator.15") + currentConf); //$NON-NLS-1$
//			cmakeArgs.add("-DCMAKE_TOOLCHAIN_FILE=" + currentToolchainFile);
			cmakeArgs.add(ManagedMakeMessages.getString("CMakeMakefileGenerator.16")); //$NON-NLS-1$

			// add cmake extra FLAGS
			
			// add some cmake options
			
			IContributedEnvironment ice = CCorePlugin.getDefault().getBuildEnvironmentManager().getContributedEnvironment();
			ICConfigurationDescription cfgDesc = CoreModel.getDefault().getProjectDescription(project).getConfigurationByName(currentConf);
			IEnvironmentVariable[] envvars = ice.getVariables( cfgDesc );
			
			String[] envp = new String[envvars.length];
			for(int i = 0; i < envvars.length; i++ ) {
				envp[i] = envvars[i].getName() + "=" + envvars[i].getValue(); //$NON-NLS-1$
			}
			
			File buildDirFile = new File(buildDir);
			if(buildDirFile.exists()) {
				if(!buildDirFile.isDirectory()) {
					throw new RuntimeException(ManagedMakeMessages.getString("CMakeMakefileGenerator.18") + buildDir + ManagedMakeMessages.getString("CMakeMakefileGenerator.19")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else {
				boolean success = buildDirFile.mkdirs();
				if(success = false) {
					throw new RuntimeException(ManagedMakeMessages.getString("CMakeMakefileGenerator.20") + buildDir + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			URI workingDirectoryURI = new URI(ManagedMakeMessages.getString("CMakeMakefileGenerator.22") + buildDir); //$NON-NLS-1$
			
			IPath cmakePath = new Path(CMAKE_EXE);
			
			String[] a = new String[cmakeArgs.size()];
			buildRunnerHelper.setLaunchParameters(launcher, cmakePath, cmakeArgs.toArray(a), workingDirectoryURI, envp);
			
			ErrorParserManager epm = new ErrorParserManager(project, workingDirectoryURI, null, null);
			List<IConsoleParser> consoleParsers = new ArrayList<IConsoleParser>(); 
			buildRunnerHelper.prepareStreams(epm, consoleParsers, (org.eclipse.cdt.core.resources.IConsole) cmakeConsole, new SubProgressMonitor(monitor, 20));

			buildRunnerHelper.removeOldMarkers(project, new SubProgressMonitor(monitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			buildRunnerHelper.greeting(IncrementalProjectBuilder.FULL_BUILD, currentConf, ManagedMakeMessages.getString("CMakeMakefileGenerator.23"), true); //$NON-NLS-1$
			int state = buildRunnerHelper.build(new SubProgressMonitor(monitor, 60, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			buildRunnerHelper.close();
			buildRunnerHelper.goodbye();

			if (state != ICommandLauncher.ILLEGAL_COMMAND) {
				// buildRunnerHelper.refreshProject(currentConf, new SubProgressMonitor(monitor, 90, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
			}
			else {
				String msg = ""; //$NON-NLS-1$
				throw new CoreException(new Status(IStatus.ERROR, ManagedBuilderCorePlugin.PLUGIN_ID, msg, new Exception()));
			}
		} catch (Exception e) {
			String msg = ManagedMakeMessages.getFormattedString("ManagedMakeBuilder.message.error.build", //$NON-NLS-1$
					new String[] { project.getName(), currentConf });
			throw new CoreException(new Status(IStatus.ERROR, ManagedBuilderCorePlugin.PLUGIN_ID, msg, e));
		} finally {
			try {
				buildRunnerHelper.close();
			} catch (IOException e) {
				ManagedBuilderCorePlugin.log(e);
			}
			monitor.done();
		}
		
		return mstatus;
	}
	
//	 private IConsole findConsole(String name) {
//	      ConsolePlugin plugin = ConsolePlugin.getDefault();
//	      IConsoleManager conMan = plugin.getConsoleManager();
//	      IConsole[] existing = conMan.getConsoles();
//	      for (int i = 0; i < existing.length; i++)
//	         if (name.equals(existing[i].getName()))
//	            return existing[i];
//	      //no console found, so create a new one
//	      IConsole myConsole = new MessageConsole(name, null);
//	      conMan.addConsoles(new IConsole[]{myConsole});
//	      return myConsole;
//	   }
	 
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
	 
//	 MultiStatus  reportError(String errorMsg) {
//		 IConsole cmakeConsole = findConsole("CMake");
//		 MessageConsoleStream out = cmakeConsole.newMessageStream();
//
//		 Color col = out.getColor();
//		 out.setColor(new Color(Display.getCurrent(), 255, 0, 0));
//		 out.println("Error: " + errorMsg);
//		 out.setColor(col);
//
//		 MultiStatus status = new MultiStatus("com.rohde_schwarz.cmake4cdt", IStatus.ERROR, "CMake failed to generate build files. See CMake console for details.", null);
//		 return status;
//	 }

}
