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
import org.eclipse.cdt.cmake.CXCompInfo;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuiltinSpecsDetector;
import org.eclipse.core.resources.IProject;

public class CrossGCCBuiltinSpecsDetector extends GCCBuiltinSpecsDetector {

	@Override
	protected String getCompilerCommand(String languageId) {
		// use as fallback value
		String cmd = super.getCompilerCommand(languageId);
		
		if (currentCfgDescription != null) {
			CXCompInfo xci = null;
			String buildConfigName = currentCfgDescription.getName();
			CMakeSettings cms = Activator.getDefault().getSettings();
			IProject proj = this.currentProject;
			if(proj != null) {
				xci = cms.getXCompInfo(proj.getName(), buildConfigName);
				if( xci == null) {
					// no compiler info for current project / configuration -> try to parse it out of compile_commands.jaon
					CMakeLangSetProvider lsp = Activator.getDefault().getLangSetProvider();
					lsp.parseCompileComands(proj, this.currentCfgDescription );
					xci = cms.getXCompInfo(proj.getName(), buildConfigName);
				}
			}
			if( xci != null) {
				cmd =  xci.getxCompCmd();
			}
		}
		return cmd; 
	}
	
	@Override
	protected String getToolOptions(String languageId) {
		String sysroot = null;
		String flags = "";
		
		if (currentCfgDescription != null) {
			CXCompInfo xci = null;
			String buildConfigName = currentCfgDescription.getName();
			CMakeSettings cms = Activator.getDefault().getSettings();
			IProject proj = this.currentProject;
			if(proj != null) {
				xci = cms.getXCompInfo(proj.getName(), buildConfigName);
				if( xci == null) {
					// no compiler info for current project / configuration -> try to parse it out of compile_commands.jaon
					CMakeLangSetProvider lsp = Activator.getDefault().getLangSetProvider();
					lsp.parseCompileComands(proj, this.currentCfgDescription );
					xci = cms.getXCompInfo(proj.getName(), buildConfigName);
				}
			}
			if( xci != null) {
				flags = xci.getxCompFlags();
			}
		}

		return flags;
	}
}
