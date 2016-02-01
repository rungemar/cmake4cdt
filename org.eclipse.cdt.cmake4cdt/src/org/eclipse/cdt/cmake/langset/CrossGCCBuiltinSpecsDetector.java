/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marc-Andre Laperle (Ericsson) - initial API and implementation
 *     Martin Runge (Rohde&Schwarz) - adoptions to CMake toolchain
 *******************************************************************************/

package org.eclipse.cdt.cmake.langset;


import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.CMakeSettings;
import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuiltinSpecsDetector;
import org.eclipse.core.resources.IProject;

public class CrossGCCBuiltinSpecsDetector extends GCCBuiltinSpecsDetector {

	@Override
	protected String getCompilerCommand(String languageId) {
		// use as fallback value
		String cmd = super.getCompilerCommand(languageId);
		
		if (currentCfgDescription != null) {
			CompileCmdsHandler cmdHdl = null;
			String buildConfigName = currentCfgDescription.getName();
			CMakeSettings cms = Activator.getDefault().getSettings();
			IProject proj = this.currentProject;
			if(proj == null) {
				proj = currentCfgDescription.getProjectDescription().getProject();
			}
			if(proj != null) {
				cmdHdl = cms.getCompileCmds(proj, buildConfigName);
			}
			if( cmdHdl != null) {
				cmd =  cmdHdl.getxCompCmd();
			}
		}
		return cmd; 
	}
	
	@Override
	protected String getToolOptions(String languageId) {
		String flags = "";
		
		if (currentCfgDescription != null) {
			CompileCmdsHandler cmdHdl = null;
			String buildConfigName = currentCfgDescription.getName();
			CMakeSettings cms = Activator.getDefault().getSettings();
			IProject proj = this.currentProject;
			if(proj == null) {
				proj = currentCfgDescription.getProjectDescription().getProject();
			}
			if(proj != null) {
				cmdHdl = cms.getCompileCmds(proj, buildConfigName);
			}
			if( cmdHdl != null) {
				flags = cmdHdl.getxCompFlags();
			}
		}

		return flags;
	}
}
