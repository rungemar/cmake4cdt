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

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class PerProjectSettings {
	private HashMap<String, PerConfigSettings> m_perConfigSettings = new HashMap<String, PerConfigSettings>();

	private final IProject m_project; 
	
	public PerProjectSettings(IProject project, ICConfigurationDescription cfgDescription) {
		m_project = project;
	}
	
	public List<ICLanguageSettingEntry> getSettingEntries(ICConfigurationDescription cfgDescription, IResource rc, String languageId) {
		PerConfigSettings configSettings = null;
		
		IProject project = rc.getProject();
		IProjectDescription prjDesc;
		try {
			prjDesc = project.getDescription();
			String projName = prjDesc.getName();

			configSettings = m_perConfigSettings.get(cfgDescription.getName());
			if(configSettings == null) {
				configSettings = new PerConfigSettings( m_project, cfgDescription );
				m_perConfigSettings.put(cfgDescription.getName(), configSettings);
			}
			return configSettings.getSettingEntries(cfgDescription, rc, languageId);
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	public String getProjectName() {
		return m_project.getName();
	}
}
