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


/**
 * @author runge_m
 *
 */
public class CXCompInfo {
	
	private String projectName;
	private String configName;
	private String xCompExe;
	private String xCompPath;
	private String xCompCmd;
	private String xCompFlags;

	
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
	
	public CXCompInfo(String projectName, String configName) {
		this.projectName = projectName;
		this.configName = configName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getConfigName() {
		return configName;
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
}
