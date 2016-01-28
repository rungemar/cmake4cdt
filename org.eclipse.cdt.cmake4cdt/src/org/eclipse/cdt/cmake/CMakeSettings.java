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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.eclipse.cdt.cmake.langset.CompileCmdsHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.json.JSONException;
import org.osgi.service.prefs.BackingStoreException;

public class CMakeSettings implements PropertyChangeListener { 

	private HashMap<String, CompileCmdsHandler> mCompCmds = new HashMap<String, CompileCmdsHandler>();
	
	public CompileCmdsHandler getCompileCmds(IProject project, String configName) {
		String projectName = project.getName();
		CompileCmdsHandler cmdHdl = null;

		try {
			if(!mCompCmds.containsKey(genKey(projectName, configName) )) {
				// for this project + build config compile_command.json was not evaluated yet 
				IPath outputPath = CMakeOutputPath.getPath(project, configName);
				String filename = outputPath.append(CompileCmdsHandler.COMPILE_CMDS_FILENAME).toString();
	
				try {
					cmdHdl = new CompileCmdsHandler(project, configName, filename);
					// only setCompileCmds() if the file compile_command.json was there an could be parsed 
					cmdHdl.parseCMakeCompileCommands();
					cmdHdl.hasChanged(true);
					setCompileCmds(cmdHdl);
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
			else {
				// compile_command.json was evaluated before -> check if it has changed since then
				cmdHdl = mCompCmds.get(genKey(projectName, configName));
				if(cmdHdl.hasChanged(false)) {
					try {
						cmdHdl.parseCMakeCompileCommands();
						cmdHdl.hasChanged(true);
						setCompileCmds(cmdHdl);
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
			}
		}
		catch(BackingStoreException be) {
			be.printStackTrace();
		}
		return mCompCmds.get(genKey(projectName, configName));
	}
	
	private void setCompileCmds(CompileCmdsHandler ccmds) {
		mCompCmds.put(genKey(ccmds.getProjectName(), ccmds.getConfigName()), ccmds);
	}

	private String genKey(String projectName, String buildConfig) {
		String key = projectName + "/" + buildConfig;
		return key;
	}
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);	

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
	}

}
