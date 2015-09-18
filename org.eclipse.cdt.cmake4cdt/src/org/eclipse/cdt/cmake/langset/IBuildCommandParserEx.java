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

import org.eclipse.cdt.core.language.settings.providers.ICBuildOutputParser;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;


public interface IBuildCommandParserEx extends ICBuildOutputParser, ILanguageSettingsProvider {

	class CompileUnitInfo {
		// file name as parsed out of the compiler invocation command line.
		// To be provided by AbstractLabguageSettingsOutoutScanner 
		private String parsedResourceName = null;
		
		// current ressource: filename translated to IRessource representation inside workspace
		// To be provided by AbstractLabguageSettingsOutoutScanner 
		private IResource currentResource = null;
		
		// file name listed in comile_command.json
		// Path is absolute or relative to workDir
		private String cuFileName = null;

		// working dir while building this cu
		private IPath workDir = null;
		
		// complete command line of the compiler invocation
		private String cmdLine = null;
		
		
		CompileUnitInfo(String rcName, IResource rc) {
			this.parsedResourceName=rcName;
			this.currentResource=rc;
		}

		CompileUnitInfo(String cuFileName, IPath directory, String compileCommand) {
			this.setCuFileName(cuFileName);
			this.workDir = directory;
			this.cmdLine = compileCommand;
		}
		/**
		 * @return the parsedResourceName
		 */
		public String getParsedResourceName() {
			return parsedResourceName;
		}

		/**
		 * @param parsedResourceName the parsedResourceName to set
		 */
		public void setParsedResourceName(String parsedResourceName) {
			this.parsedResourceName = parsedResourceName;
		}

		/**
		 * @return the currentResource
		 */
		public IResource getCurrentResource() {
			return currentResource;
		}

		/**
		 * @param currentResource the currentResource to set
		 */
		public void setCurrentResource(IResource currentResource) {
			this.currentResource = currentResource;
		}

		/**
		 * @return the wdir
		 */
		public IPath getWorkDir() {
			return workDir;
		}

		/**
		 * @param wdir the wdir to set
		 */
		public void setWorkDir(IPath wDir) {
			this.workDir = wDir;
		}

		/**
		 * @return the cmdLine
		 */
		public String getCmdLine() {
			return cmdLine;
		}

		/**
		 * @param cmdLine the cmdLine to set
		 */
		public void setCmdLine(String cmdLine) {
			this.cmdLine = cmdLine;
		}

		/**
		 * @return the cuFileName
		 */
		public String getCuFileName() {
			return cuFileName;
		}

		/**
		 * @param cuFileName the cuFileName to set
		 */
		public void setCuFileName(String cuFileName) {
			this.cuFileName = cuFileName;
		}
	}
	
	public CompileUnitInfo getCompileUnitInfo();
	
}
