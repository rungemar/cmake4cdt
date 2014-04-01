package com.rohde_schwarz.cmake4cdt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
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
	
	public final String CMAKE_EXE = "cmake";

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
		IEclipsePreferences projectProperties = new ProjectScope(project).getNode("com.rohde_schwarz.cmake4cdt.scope");
		boolean buildDirWorkspaceSettings = true;
		if (projectProperties != null) {
			buildDirWorkspaceSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);
		}
		
		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		
		// make sure, that current project location is stored in ${BuildIF_ProjectPath}
		IPath projDir = project.getLocation();
		IValueVariable cmakeProjectDirVar = varMgr.getValueVariable("CMake_ProjectPath");
		cmakeProjectDirVar.setValue(projDir.toString());

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
		// @TODO get settings for selected CMake generator and adjust Makefile Name (e.g. in case of ninja make)
		return "Makefile";
	}

	@Override
	public void initialize(IProject project, IManagedBuildInfo info,
			IProgressMonitor monitor) {
		this.project = project;
		this.buildInfo = info; 
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
		MultiStatus mstatus = new MultiStatus("com.rohde_schwarz.cmake4cdt.builder", 0, "success", null );

		MessageConsole buildifConsole = findConsole("CMake");
		MessageConsoleStream out = buildifConsole.newMessageStream();

		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();

//		IValueVariable buildifProjectDirVar = varMgr.getValueVariable("BuildIF_ProjectPath");
//		buildifProjectDirVar.setValue(projDir.toString());
		
		// IConfiguration activeConfig = buildInfo.getDefaultConfiguration();
		String currentConf = buildInfo.getConfigurationName();
		String currentArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
		String currentInstrument = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);
		
		String buildDir = null;
		
		IEclipsePreferences projectProperties = new ProjectScope(project).getNode("com.rohde_schwarz.cmake4cdt.scope");
		boolean instSpecific = false;
		boolean useWorkspaceBuidDirSettings = true;
		if (projectProperties != null) {
			instSpecific = projectProperties.getBoolean(CMakePropertyConstants.P_IS_INSTRUMENT_SPECIFIC, false);
			useWorkspaceBuidDirSettings = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, true);

			buildDir = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);

			if(!useWorkspaceBuidDirSettings) {
				buildDir = projectProperties.get( CMakePropertyConstants.P_BUILD_PATH, buildDir);
			}
			
		}

		IValueVariable destdirVar = varMgr.getValueVariable("CMake_Destdir");
		String destDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_DESTDIR);
		destDirStr = varMgr.performStringSubstitution(destDirStr);
		destdirVar.setValue( destDirStr );

		
		buildDir = varMgr.performStringSubstitution(buildDir);
		List<String> cmakeCmd = new ArrayList<String>();
		
		cmakeCmd.add( CMAKE_EXE );

		cmakeCmd.add("-DCMAKE_BUILD_TYPE=");
		cmakeCmd.add("-DCMAKE_TOOLCHAIN_FILE=");
		
		cmakeCmd.add("-DCMAKE_EXPORT_COMPILE_COMMANDS=on");

		// add cmake extra FLAGS
		
		// add some cmake options
		
		if(instSpecific &&  currentInstrument != PreferenceConstants.P_NOT_INST_SPECIFIC ) {
			cmakeCmd.add("-i " + currentInstrument);
		}
		
		if(buildDir != null) {
			cmakeCmd.add("-g " + buildDir);
		}
		// String name = project.getName();

		String message = new String("invoking: ");
		for(int i=0; i < cmakeCmd.size(); i++)
		{
			message += cmakeCmd.get(i);
		}
		// out.println(message);
		
		ProcessBuilder pb = new ProcessBuilder(cmakeCmd);
		// ProcessBuilder pb = new ProcessBuilder("env");
		IPath wdir = project.getLocation();
		pb.directory(wdir.toFile());
		pb.redirectErrorStream(true);
		
		message = "working dir: " + wdir;
		out.println( message );
		
//		Map<String,String> env = pb.environment();
//		env.put("INSTRUMENT", currentInstrument);

		try { 
			Process proc = pb.start();
			InputStream instream = proc.getInputStream();
			
			proc.waitFor();
			
			String buildifOutput = inputStream2String(instream);
			out.print(buildifOutput);

			Status status = new Status( org.eclipse.core.runtime.IStatus.OK, "com.rohde_schwarz.cmake4cdt", 0, "success", null);
			mstatus.add(status);
			
		} catch (Exception ex) {
			return reportError(ex.getMessage());
		}
		
		return mstatus;
	}
	
	 private MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
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
				 sb.append(line + "\n");
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
	 
	 MultiStatus  reportError(String errorMsg) {
		 MessageConsole buildifConsole = findConsole("CMake");
		 MessageConsoleStream out = buildifConsole.newMessageStream();

		 Color col = out.getColor();
		 out.setColor(new Color(Display.getCurrent(), 255, 0, 0));
		 out.println("Error: " + errorMsg);
		 out.setColor(col);

		 MultiStatus status = new MultiStatus("com.rohde_schwarz.cmake4cdt", IStatus.ERROR, "CMake failed to generate build files. See CMake console for details.", null);
		 return status;
	 }

}
