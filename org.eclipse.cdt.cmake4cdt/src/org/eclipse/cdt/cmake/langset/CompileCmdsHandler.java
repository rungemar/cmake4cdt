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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.cmake.langset.IBuildCommandParserEx.CompileUnitInfo;
import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author runge_m
 *
 */
public class CompileCmdsHandler {

	private String filename;
	private List<CompileUnitInfo> foreignSources = new ArrayList<CompileUnitInfo>();
	private List<CompileUnitInfo> sources = new ArrayList<CompileUnitInfo>();
	
	CompileCmdsHandler(String filename) {
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
	 */
	public boolean hasChanged() {
		// TODO Auto-generated method stub
		return true;
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
