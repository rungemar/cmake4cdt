/*******************************************************************************
 * Copyright (c) 2014, 2015 Rohde & Schwarz GmbH & Co. KG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Runge - initial implementation of cmake support
 *******************************************************************************/
package org.eclipse.cdt.cmake.langset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsBaseProvider;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;



public class CMakeLangSetProvider extends LanguageSettingsBaseProvider
		implements ILanguageSettingsProvider {

	private HashMap<String, PerProjectSettings> m_perProjSettings = new HashMap<String, PerProjectSettings>();

	public CMakeLangSetProvider() {
		// TODO Auto-generated constructor stub
	}

	public CMakeLangSetProvider(String id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public CMakeLangSetProvider(String id, String name, List<String> languages,
			List<ICLanguageSettingEntry> entries) {
		super(id, name, languages, entries);
		// TODO Auto-generated constructor stub
	}

	public CMakeLangSetProvider(String id, String name, List<String> languages,
			List<ICLanguageSettingEntry> entries, Map<String, String> properties) {
		super(id, name, languages, entries, properties);
		// TODO Auto-generated constructor stub
	}

	public List<ICLanguageSettingEntry> getSettingEntries(ICConfigurationDescription cfgDescription, IResource rc, String languageId) {
		
		PerProjectSettings projSettings = null;
		if(cfgDescription == null || rc == null) {
			return null;
		}
		
		IProject project = rc.getProject();
		IProjectDescription prjDesc;
		try {
			prjDesc = project.getDescription();
			String projName = prjDesc.getName();

			projSettings = m_perProjSettings.get(projName);
			if(projSettings == null) {
				projSettings = new PerProjectSettings(project, cfgDescription);
				m_perProjSettings.put(projName, projSettings);
			}
			List<ICLanguageSettingEntry> entries = projSettings.getSettingEntries(cfgDescription, rc, languageId); 
			return entries;
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
//		List<ICLanguageSettingEntry> settings = new ArrayList<ICLanguageSettingEntry>();
//		
//		settings.add( CDataUtil.createCIncludePathEntry("/home/buildsys4/x86/i686-pc-linux-gnu/i686-pc-linux-gnu/qt-4.7.4/include/Qt", ICSettingEntry.INCLUDE_PATH) );
//		settings.add( CDataUtil.createCIncludePathEntry("/home/buildsys4/x86/i686-pc-linux-gnu/i686-pc-linux-gnu/qt-4.7.4/include/QtGui", ICSettingEntry.INCLUDE_PATH) );
//		settings.add( CDataUtil.createCIncludePathEntry("/home/buildsys4/x86/i686-pc-linux-gnu/i686-pc-linux-gnu/qt-4.7.4/include/QtCore", ICSettingEntry.INCLUDE_PATH) );
//		return settings; 
	}
	

}
