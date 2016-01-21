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

package org.eclipse.cdt.cmake.langset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.CMakeMakefileGenerator;
import org.eclipse.cdt.cmake.CMakeSettings;
import org.eclipse.cdt.cmake.CXCompInfo;
import org.eclipse.cdt.cmake.langset.IBuildCommandParserEx.CompileUnitInfo;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author runge_m
 *
 */
public class CompileCmdsHandler {

	private ICConfigurationDescription cfgdesc = null;
	private IProject project = null;
	private String filename = null;
	private List<CompileUnitInfo> foreignSources = new ArrayList<CompileUnitInfo>();
	private List<CompileUnitInfo> sources = new ArrayList<CompileUnitInfo>();
	
	public static final String CROSS_GCC_TOOL_ID = "org.eclipse.cdt.cmake.compiler.c"; 
	public static final String CROSS_GPP_TOOL_ID = "org.eclipse.cdt.cmake.compiler.c++";
	//public static final String CROSS_TOOLCHAIN_PREFIX_OPTION_ID = "cdt.managedbuild.option.gnu.cross.prefix";
	public static final String CROSS_TOOLCHAIN_PREFIX_OPTION_ID = "org.eclipse.cdt.cmake.cross.prefix";
	public static final String CROSS_TOOLCHAIN_PATH_OPTION_ID = "cdt.managedbuild.option.gnu.cross.path";

	CompileCmdsHandler(ICConfigurationDescription cfgDesc, IProject project, String filename) {
		this.cfgdesc = cfgDesc;
		this.project = project;
		this.filename = filename;
	}
	
	public void parseCMakeCompileCommands() {
		foreignSources.clear();
		sources.clear();
		
		try {
			
			FileReader reader = new FileReader(this.filename);
			JSONTokener tokener = new JSONTokener(reader);
			JSONArray rootArray = new JSONArray(tokener);
			
			for(int i=0; i < rootArray.length(); i++) {

				Object obj = rootArray.get(i);
				if(obj instanceof JSONObject) {
					JSONObject jobj = (JSONObject)obj;
					
					String directory = jobj.getString("directory");
					String command = jobj.getString("command");
					String sourceFile = jobj.getString("file");

					CompileUnitInfo cu = new CompileUnitInfo(sourceFile, new Path(directory), command);
					sources.add(cu);
//					cwdTracker.setWorkingDirectoryURI(directory);
//					m_commandParser.processLine(command);
//					CompileUnitInfo cuInfo = m_commandParser.getCompileUnitInfo();
//					if(cuInfo.getCurrentResource() == null) {
//						// the following file is not a project Resource, but gets compiled.
//						foreignSources.add(cuInfo);
//					}
				}
			}
		}
		catch(FileNotFoundException fex) {
			System.out.printf("Could not open json file: %s", fex.getMessage());
		}
		catch(JSONException jex) {
			System.out.printf("JSONException: %s", jex.getMessage());
		}
		finally {
			
		}
	}

	public boolean isOutsideProject(CompileUnitInfo cu) {

		return false;
	}
	
	protected void detectCompiler()  {

		if(sources.size() < 1 ) {
			return;
		}
		
		CompileUnitInfo cui = sources.get(0);

		String compilerCommand = "";
		String flags = "";
		String sysrootPath = "";
		boolean nextPartIsSysrootPath = false;

		String cmd = cui.getCmdLine();
		String[] parts = cmd.split(" ");
		for(String part: parts) {
			
			if(part.endsWith("gcc") || part.endsWith("g++") || part.endsWith("c++") || part.endsWith("cc") ) {
				// this is the compiler part
				compilerCommand = part ;
				continue;
			}
			if(part.startsWith("--sysroot")) {
				if(part.startsWith("--sysroot=")) {
					// path in included part 
					/// TODO: handle path with whitespaces which would be continues in next part 
					sysrootPath = part.substring("--sysroot=".length());
				}
				else {
					// path is in next part 
					/// TODO: handle path with whitespaces which would be continues in next part 
					nextPartIsSysrootPath = true;					
				}
				continue;
			}
			if(nextPartIsSysrootPath == true) {
				sysrootPath = part;
				nextPartIsSysrootPath = false;
			}
		}
		
	
//		// Pattern sysrootRegexp = Pattern.compile("\\-\\-sysroot[\\s=]([\\\"']?)(.*)\\1");
//		Pattern sysrootRegexp = Pattern.compile("\\-\\-sysroot[=\\s]([\\\"'])(.*)\\1");
//		Matcher sysrootMatcher = sysrootRegexp.matcher(cmd);
//	    // check all occurance
//	    if(sysrootMatcher.find()) {
//	    	flags += " " + sysrootMatcher.group(2); 
//	    }
	    
		flags += "--sysroot " + sysrootPath;

	    setXCompInfo( compilerCommand, flags );
	}

	private void setXCompInfo( String compilerCommand, String flags) {
		CMakeSettings cms = Activator.getDefault().getSettings();
		String buildConfigName = cfgdesc.getName();

		java.nio.file.Path compCmd = Paths.get(compilerCommand);
		String compDir = compCmd.getParent().toString();
		String compExe = compCmd.getFileName().toString();
		
		
		CXCompInfo xci =  cms.getXCompInfo( project.getName(), buildConfigName );
		
		if (xci == null) {
			xci = new CXCompInfo(project.getName(), buildConfigName);
		}
		
		xci.setxCompCmd(compilerCommand );
		xci.setxCompExe( compExe );
		xci.setxCompPath( compDir );
		
		xci.setxCompFlags( flags );
		
		cms.setXCompInfo( xci );
	}
	
	private void setCompilerTool( String compilerCommand ) {
		String compPrefix = "";
		try {
			java.nio.file.Path compCmd = Paths.get(compilerCommand);
			String compDir = compCmd.getParent().toString();
			String compFn = compCmd.getFileName().toString();
			
			Pattern pattern = Pattern.compile("(.*?)g\\+\\+");
			Matcher matcher = pattern.matcher(compFn);
			if (matcher.find())
			{
			    compPrefix = matcher.group(1);
			}
			
	        IResourceInfo resourceInfos[] = ManagedBuildManager.getConfigurationForDescription(cfgdesc).getResourceInfos();
	        IResourceInfo resourceInfo = resourceInfos[0];
	
	        ITool cppCompTool = getCppCompilerTool();
	//      String cmd = cCompTool.getToolCommand();
	//      cCompTool.setToolCommand( compilerCommand );
	
	        IConfiguration cfg = ManagedBuildManager.getConfigurationForDescription(cfgdesc);
	        IToolChain tc = cfg.getToolChain();
	
//			IOption option = tc.getOptionById("cdt.managedbuild.option.gnu.cross.prefix"); //$NON-NLS-1$
			
			
	        IOption prefixOptionTmpl = cppCompTool.getOptionBySuperClassId( "org.eclipse.cdt.cmake.c++.prefix" );
	        IOption prefixOption = cppCompTool.getOptionToSet(prefixOptionTmpl, false);
			prefixOption.setValue(compPrefix);
	        ManagedBuildManager.setOption(resourceInfo, cppCompTool, prefixOption, compPrefix);
	//
	//		IOption builyTypeOptionTmpl = cmakeTool.getOptionById(CMAKE_OPTION_BUILDTYPE);
	//		IOption builyTypeOption = cmakeTool.getOptionToSet(builyTypeOptionTmpl, false);
	//		builyTypeOption.setValue(m_buildType);
	//        ManagedBuildManager.setOption(resourceInfo, cmakeTool, toolchainfileOption, m_toolchainFile);
	
	        
	        // ------ Save this business to disk.
	        ManagedBuildManager.saveBuildInfo(cfgdesc.getProjectDescription().getProject(), true);
		} catch (BuildException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}

	private ITool getCCompilerTool() {
		return getToolByID(CompileCmdsHandler.CROSS_GCC_TOOL_ID);
	}

	private ITool getCppCompilerTool() {
		return getToolByID(CompileCmdsHandler.CROSS_GPP_TOOL_ID);
	}

	private ITool getToolByID(String id) {
		IConfiguration cfg = ManagedBuildManager.getConfigurationForDescription(cfgdesc);
		ITool[] tools = cfg.getToolsBySuperClassId(id);
		ITool tool = null;
		if(tools.length > 0) {
			tool = tools[0];
		}
		return tool;
	}
	
	/**
	 * @return
	 */
	public boolean hasSourcesOutsideProject() {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * @return
	 * @throws BackingStoreException 
	 */
	public boolean hasChanged(boolean resetModifiedTimestamp) throws BackingStoreException  {

		ProjectScope ps = new ProjectScope(project);
		IEclipsePreferences prefs = ps.getNode( Activator.getId() );
		
		String key = this.filename;
		Long value = prefs.getLong(key, 0);
		
		File f = new File(this.filename);
		Long current_ts = f.lastModified();
		
		if(resetModifiedTimestamp) {
			prefs.putLong(key, current_ts);
			prefs.flush();
		}
		
		if(value.equals(current_ts)) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * @return
	 */
	public List<CompileUnitInfo> getSources() {
		return sources;
	}

	/**
	 * @return
	 */
	public List<CompileUnitInfo> getSourcesOutsideProject() {
		return foreignSources;
	}
}
