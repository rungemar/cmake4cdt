package org.eclipse.cdt.cmake.langset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.cmake.CMakeOutputPath;
import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsBaseProvider;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.make.internal.core.scannerconfig.gnu.GCCScannerInfoConsoleParser;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;



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
			return projSettings.getSettingEntries(cfgDescription, rc, languageId);
			
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
