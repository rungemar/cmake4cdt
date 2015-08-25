/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.cmake.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.cdt.cmake.CMakeProjectGenerator;
import org.eclipse.cdt.managedbuilder.ui.properties.ManagedBuilderUIPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class ImportCMakeWizard extends Wizard implements IImportWizard {
	
	ImportCMakeWizardPage mainPage;

	public ImportCMakeWizard() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		
		final String projectName = mainPage.getProjectName();
		final String locationStr = mainPage.getLocation();
		final URI locationURI    = mainPage.getLocationURI();
		final boolean isCPP = mainPage.isCPP();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject newProject = workspace.getRoot().getProject(projectName);

		CMakeProjectGenerator pg = new CMakeProjectGenerator(newProject);
		pg.createProject(this, locationURI);
		
		
		IRunnableWithProgress op = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException,
					InterruptedException {
				monitor.beginTask("Ctreating CMake project ...", 10);

				// Create Project
				try {
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IProject project = workspace.getRoot().getProject(projectName);
					
					// TODO handle the case where a .project file was already there

					IProjectDescription description = workspace.newProjectDescription(projectName);
					IPath defaultLocation = workspace.getRoot().getLocation().append(projectName);
					Path location = new Path(locationStr);
					if (!location.isEmpty() && !location.equals(defaultLocation)) {
						description.setLocation(location);
					}
					

					
//					ICProjectDescriptionManager pdMgr = CoreModel.getDefault().getProjectDescriptionManager();
//					ICProjectDescription projDesc = pdMgr.createProjectDescription(project, false);
//					ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
//					ManagedProject mProj = new ManagedProject(projDesc);
//					info.setManagedProject(mProj);
					monitor.worked(1);

					
					CMakeProjectGenerator cpGen = new CMakeProjectGenerator( project );
					cpGen.setupProject( monitor );
					
					monitor.worked(1);

				} catch (Throwable e) {
					ManagedBuilderUIPlugin.log(e);
				}
				monitor.done();
			}
		};

		try {
			getContainer().run(true, true, op);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	 
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("File Import Wizard"); //NON-NLS-1
		setNeedsProgressMonitor(true);
		mainPage = new ImportCMakeWizardPage("Import CMake project"); //NON-NLS-1
	}
	
	/* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        super.addPages(); 
        addPage(mainPage);        
    }

}
