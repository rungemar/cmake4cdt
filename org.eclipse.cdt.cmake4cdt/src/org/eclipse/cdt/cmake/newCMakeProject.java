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

import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.cdt.core.templateengine.process.processes.Messages;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.templateengine.ProjectCreatedActions;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

// most of this file was copied from /org.eclipse.cdt.autotools.core/src/org/eclipse/cdt/internal/autotools/core/wizards/NewAutotoolsProject.java

public class newCMakeProject extends ProcessRunner {

	protected boolean savedAutoBuildingValue;
	protected ProjectCreatedActions pca;
	protected IManagedBuildInfo info;
	
	public newCMakeProject() {
		pca = new ProjectCreatedActions();
	}

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
		                                         throws ProcessFailureException {

		String projectName = args[0].getSimpleValue();
		String location = args[1].getSimpleValue();
//		String artifactExtension = args[2].getSimpleValue();
		String isCProjectValue = args[2].getSimpleValue();
		boolean isCProject = Boolean.valueOf(isCProjectValue).booleanValue();
				
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		try {
			if (!project.exists()) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				turnOffAutoBuild(workspace);

				IPath locationPath = null;
				if (location != null && !location.trim().equals("")) { //$NON-NLS-1$
					locationPath = Path.fromPortableString(location);
				}

				List<?> configs = template.getTemplateInfo().getConfigurations();
				if (configs == null || configs.size() == 0) {
					throw new ProcessFailureException(Messages.getString("NewManagedProject.4") + projectName); //$NON-NLS-1$
				}

				pca.setProject(project);
				pca.setProjectLocation(locationPath);
				pca.setConfigs((IConfiguration[]) configs.toArray(new IConfiguration[configs.size()]));
				//pca.setArtifactExtension(artifactExtension);
				info = pca.createProject(monitor, CCorePlugin.DEFAULT_INDEXER, true);

				CMakeProjectNature.addNature(project, monitor);
				
				// For each IConfiguration, create a corresponding Autotools Configuration
				IConfiguration[] cfgs = pca.getConfigs();
				for (int i = 0; i < cfgs.length; ++i) {
					IConfiguration cfg = cfgs[i];
					ICConfigurationDescription cfgd = ManagedBuildManager.getDescriptionForConfiguration(cfg);
					String id = cfgd.getId();
					// AutotoolsConfigurationManager.getInstance().getConfiguration(project, id, true);
				}
				// AutotoolsConfigurationManager.getInstance().saveConfigs(project);

				info.setValid(true);
				ManagedBuildManager.saveBuildInfo(project, true);

				restoreAutoBuild(workspace);

			}
			else {
				CMakeProjectNature.addNature(project, monitor);
				ManagedBuildManager.saveBuildInfo(project, true);
				//			throw new ProcessFailureException(Messages.getString("NewAutotoolsProject.5") + projectName); //$NON-NLS-1$
			}
		} catch (CoreException e) {
			throw new ProcessFailureException(Messages.getString("NewManagedProject.3") + e.getMessage(), e); //$NON-NLS-1$
		} catch (BuildException e) {
			throw new ProcessFailureException(Messages.getString("NewManagedProject.3") + e.getMessage(), e); //$NON-NLS-1$
		}
	}

	// copied from /org.eclipse.cdt.autotools.core/src/org/eclipse/cdt/internal/autotools/core/wizards/NewAutotoolsProject.java
	protected final void turnOffAutoBuild(IWorkspace workspace) throws CoreException {
		IWorkspaceDescription workspaceDesc = workspace.getDescription();
		savedAutoBuildingValue = workspaceDesc.isAutoBuilding();
		workspaceDesc.setAutoBuilding(false);
		workspace.setDescription(workspaceDesc);
	}
	
	// copied from /org.eclipse.cdt.autotools.core/src/org/eclipse/cdt/internal/autotools/core/wizards/NewAutotoolsProject.java
	protected final void restoreAutoBuild(IWorkspace workspace) throws CoreException {
		IWorkspaceDescription workspaceDesc = workspace.getDescription();
		workspaceDesc.setAutoBuilding(savedAutoBuildingValue);
		workspace.setDescription(workspaceDesc);
	}

}
