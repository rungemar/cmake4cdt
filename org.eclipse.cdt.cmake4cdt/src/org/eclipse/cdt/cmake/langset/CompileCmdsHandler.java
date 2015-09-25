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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.langset.IBuildCommandParserEx.CompileUnitInfo;
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

	private IProject project = null;
	private String filename = null;
	private List<CompileUnitInfo> foreignSources = new ArrayList<CompileUnitInfo>();
	private List<CompileUnitInfo> sources = new ArrayList<CompileUnitInfo>();

	CompileCmdsHandler(IProject project, String filename) {
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
