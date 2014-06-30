package org.eclipse.cdt.cmake.langset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.cmake.CMakeOutputPath;
import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsEditableProvider;
import org.eclipse.cdt.core.language.settings.providers.IWorkingDirectoryTracker;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.cdt.make.internal.core.scannerconfig.gnu.GCCScannerInfoConsoleParser;
import org.eclipse.cdt.managedbuilder.language.settings.providers.AbstractBuildCommandParser;
import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuildCommandParser;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PerConfigSettings {
	private final ICConfigurationDescription m_cfgDesc; 
	private final IProject m_project;
	private AbstractBuildCommandParser m_commandParser;
	private final IPath m_outputPath;
	
	private final CMakeCompileCmdsCwdTracker cwdTracker;
	
	public PerConfigSettings(IProject project, ICConfigurationDescription cfgDescription) {
		m_project = project;
		m_cfgDesc = cfgDescription;
		m_commandParser = null;
		cwdTracker = new CMakeCompileCmdsCwdTracker();

		m_outputPath = CMakeOutputPath.getPath(m_project, m_cfgDesc.getName());
	}
	
	public List<ICLanguageSettingEntry> getSettingEntries(ICConfigurationDescription cfgDescription, IResource rc, String languageId) throws CoreException {
		if(m_commandParser == null) {
			// try to figure out which compiler was used. Assume gcc for now
			m_commandParser = new GCCBuildCommandParser();
			m_commandParser.startup(m_cfgDesc, cwdTracker);
			parseCMakeCompileCommands();
			
		}
		
		return m_commandParser.getSettingEntries(cfgDescription, rc, languageId);
	}

	public ICConfigurationDescription getConfigDesc() {
		return m_cfgDesc;
	}
	
	public void parseCMakeCompileCommands() {
		
		IPath jsonFile = m_outputPath.append("compile_commands.json");
		try {
			
			FileReader reader = new FileReader(jsonFile.toFile());
			JSONTokener tokener = new JSONTokener(reader);
			JSONArray rootArray = new JSONArray(tokener);
			
			for(int i=0; i < rootArray.length(); i++) {

				Object obj = rootArray.get(i);
				if(obj instanceof JSONObject) {
					JSONObject jobj = (JSONObject)obj;
					
					String directory = jobj.getString("directory");
					String command = jobj.getString("command");
					String sourceFile = jobj.getString("file");

					cwdTracker.setWorkingDirectoryURI(directory);
					m_commandParser.processLine(command);
				}
			}
		}
		catch(FileNotFoundException fex) {
			System.out.printf("Could not open json file: %s", fex.getMessage());
		}
		catch(JSONException jex) {
			System.out.printf("JSONException: %s", jex.getMessage());
		}
	}

	
	protected void detectCompiler()  {
		
	}
	
	class CMakeCompileCmdsCwdTracker implements IWorkingDirectoryTracker {

		private URI workingDirectoryURI;
		/**
		 * @param workingDirectoryURI the workingDirectoryURI to set
		 */
		public void setWorkingDirectoryURI(URI workingDirectoryURI) {
			this.workingDirectoryURI = workingDirectoryURI;
		}
		
		public void setWorkingDirectoryURI(String workingDirectory) {
			try {
				this.workingDirectoryURI = new URI( null, null, workingDirectory);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.cdt.core.language.settings.providers.IWorkingDirectoryTracker#getWorkingDirectoryURI()
		 */
		@Override
		public URI getWorkingDirectoryURI() {
			return this.workingDirectoryURI; 
		}
	}

}
