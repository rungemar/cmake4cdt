package org.eclipse.cdt.cmake.langset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.eclipse.cdt.cmake.CMakeMakefileGenerator;
import org.eclipse.cdt.cmake.CMakeOutputPath;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.ProblemMarkerInfo;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector;
import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoConsoleParser;
import org.eclipse.cdt.make.core.scannerconfig.ScannerInfoTypes;
import org.eclipse.cdt.make.internal.core.scannerconfig.gnu.GCCPerFileBOPConsoleParser;
import org.eclipse.cdt.make.internal.core.scannerconfig.gnu.GCCScannerInfoConsoleParser;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

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
				m_perConfigSettings.put(projName, configSettings);
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
