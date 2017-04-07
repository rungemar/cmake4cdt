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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.langset.IBuildCommandParserEx.CompileUnitInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Path;
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

	private String configName = null;
	private IProject project = null;
	private String filename = null;

	private String xCompExe;
	private String xCompPath;
	private String xCompCmd;
	private String xCompFlags;
	
	private List<CompileUnitInfo> foreignSources = new ArrayList<CompileUnitInfo>();
	private List<CompileUnitInfo> sources = new ArrayList<CompileUnitInfo>();
	
	public static final String COMPILE_CMDS_FILENAME = "compile_commands.json";
	public static final String CROSS_GCC_TOOL_ID = "org.eclipse.cdt.cmake.compiler.c"; 
	public static final String CROSS_GPP_TOOL_ID = "org.eclipse.cdt.cmake.compiler.c++";
	//public static final String CROSS_TOOLCHAIN_PREFIX_OPTION_ID = "cdt.managedbuild.option.gnu.cross.prefix";
	public static final String CROSS_TOOLCHAIN_PREFIX_OPTION_ID = "org.eclipse.cdt.cmake.cross.prefix";
	public static final String CROSS_TOOLCHAIN_PATH_OPTION_ID = "cdt.managedbuild.option.gnu.cross.path";

	public CompileCmdsHandler(IProject project, String configName, String filename) {
		this.configName = configName;
		this.project = project;
		this.filename = filename;
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	public Long getFileModTime() {
		File f = new File(this.filename);
		Long current_ts = f.lastModified();
		return current_ts;
	}

	public String getProjectName() {
		return this.project.getName();
	}

	public String getConfigName() {
		return this.configName;
	}
	
	public String getxCompFlags() {
		return xCompFlags;
	}
	public void setxCompFlags(String xCompFlags) {
		this.xCompFlags = xCompFlags;
	}

	
	public String getxCompExe() {
		return xCompExe;
	}
	public void setxCompExe(String xCompExe) {
		this.xCompExe = xCompExe;
	}

	public String getxCompCmd() {
		return xCompCmd;
	}
	public void setxCompCmd(String xCompCmd) {
		this.xCompCmd = xCompCmd;
	}

	public String getxCompPath() {
		return xCompPath;
	}
	public void setxCompPath(String compilerPath) {
		this.xCompPath = compilerPath;
	}
	
	public void parseCMakeCompileCommands() throws FileNotFoundException, JSONException  {
		foreignSources.clear();
		sources.clear();
		
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
				
				detectCompiler();
			}
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
		
		boolean compilerFound = false;
		
		for(String part: parts) {
		
			if(compilerFound == false) {
				if(!part.startsWith("-")) {
					if(part.endsWith("gcc") || part.endsWith("g++") || part.endsWith("c++") || part.endsWith("cc") ) {
						// this is the compiler part
						compilerCommand = part ;
						xCompCmd = compilerCommand;

						java.nio.file.Path compCmd = Paths.get(compilerCommand);
					    java.nio.file.Path ppath = compCmd.getParent();
					    if(ppath != null) {
					    	xCompPath = ppath.toString();
					    }
					    xCompExe = compCmd.getFileName().toString();
					    compilerFound = true;
						continue;
					}
				}
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
		
	
	    if(!sysrootPath.isEmpty()) {
	    	flags += "--sysroot " + sysrootPath;
	    }


	    xCompFlags = flags;
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
		
		Long current_ts = getFileModTime();
		
		if(resetModifiedTimestamp) {
			prefs.putLong(key, current_ts);
			prefs.flush();
		}
		
		if(value.longValue() == current_ts.longValue()) {
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
