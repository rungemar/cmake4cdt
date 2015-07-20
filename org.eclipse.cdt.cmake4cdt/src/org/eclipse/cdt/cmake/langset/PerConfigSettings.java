package org.eclipse.cdt.cmake.langset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.cmake.CMakeOutputPath;
import org.eclipse.cdt.cmake.langset.IBuildCommandParserEx.CompileUnitInfo;
import org.eclipse.cdt.core.language.settings.providers.IWorkingDirectoryTracker;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PerConfigSettings {
	private final ICConfigurationDescription m_cfgDesc; 
	private final IProject m_project;
	// private AbstractBuildCommandParser m_commandParser;
	private IBuildCommandParserEx m_commandParser;
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
			// m_commandParser = new GCCBuildCommandParser();
			m_commandParser = new CMakeCompileCommandParserGCC();
			m_commandParser.startup(m_cfgDesc, cwdTracker);
			// m_commandParser.setResourceScope(ResourceScope.PROJECT);
			parseCMakeCompileCommands();
			cleanupSettingsEntries();
			m_commandParser.shutdown();
		}
		
		return m_commandParser.getSettingEntries(cfgDescription, rc, languageId);
	}


	public ICConfigurationDescription getConfigDesc() {
		return m_cfgDesc;
	}
	
	class AddForeignSourcesWorkspaceJob extends WorkspaceJob {

		private List<CompileUnitInfo> cuInfoList;

		/**
		 * @param name
		 */
		public AddForeignSourcesWorkspaceJob(String name, List<CompileUnitInfo> cuInfos) {
			super(name);
			cuInfoList = cuInfos;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor)	throws CoreException {
			try {
				IFolder fsFolder = m_project.getFolder(new Path("ForeignSources"));
				if(!fsFolder.exists()) {
					fsFolder.create(IResource.VIRTUAL, true, null);
				}
				for(CompileUnitInfo cuInfo: cuInfoList) {
					File fsFileName = new File(cuInfo.getParsedResourceName());
					IFile fsFile = fsFolder.getFile(fsFileName.getName());
					IPath linkTarget = new Path(cuInfo.getParsedResourceName());
					fsFile.createLink(linkTarget, IResource.REPLACE, null);
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Status.OK_STATUS;
		}
	}
	
	public void parseCMakeCompileCommands() {
		
		IPath jsonFile = m_outputPath.append("compile_commands.json");
		List<CompileUnitInfo> foreignSources = new ArrayList<CompileUnitInfo>();
		
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
					CompileUnitInfo cuInfo = m_commandParser.getCompileUnitInfo();
					if(cuInfo.getCurrentResource() == null) {
						// the following file is not a project Resource, but gets compiled.
						foreignSources.add(cuInfo);
					}
				}
			}
		}
		catch(FileNotFoundException fex) {
			System.out.printf("Could not open json file: %s", fex.getMessage());
		}
		catch(JSONException jex) {
			System.out.printf("JSONException: %s", jex.getMessage());
		}
		finally {
			if(!foreignSources.isEmpty()) {
				Job job = new AddForeignSourcesWorkspaceJob("Creating \"Foreign Sources\" folders", foreignSources); 
				job.schedule();
			}
		}
	}

	/**
	 * 
	 */
	private void cleanupSettingsEntries() {
		//m_commandParser.getSettingEntries(cfgDescription, rc, languageId);
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
			if (!this.workingDirectoryURI.isAbsolute()) {
				System.out.println(this.workingDirectoryURI.toString());
			}
		}
		
		public void setWorkingDirectoryURI(String workingDirectory) {
			try {
				this.workingDirectoryURI = new URI( "file", workingDirectory, null);
				if (!this.workingDirectoryURI.isAbsolute()) {
					System.out.println(this.workingDirectoryURI.toString());
				}
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
