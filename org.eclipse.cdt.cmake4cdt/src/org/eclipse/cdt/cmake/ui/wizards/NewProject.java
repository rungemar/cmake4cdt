/*******************************************************************************
 * Copyright (c) 2014 Rohde & Schwarz GmbH & Co. KG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     This class was heavily quoted from org.eclipse.cdt.arduino.core.ArduinoProjectGenerator
 *     Martin Runge - initial implementation of cmake support
 *******************************************************************************/

package org.eclipse.cdt.cmake.ui.wizards;

import org.eclipse.cdt.cmake.CMakeProjectGenerator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class NewProject extends BasicNewProjectResourceWizard {

	private NewCMakeProjectPage page;
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		super.addPages();
	}

		/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		// final String containerName = page.getProjectName();
		// final String fileName = page.getFileName();
		
		if(!super.performFinish()) {
			return false;
		}
		
		new Job("New CMake Project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					final CMakeProjectGenerator generator = new CMakeProjectGenerator(getNewProject());
					generator.setupProject(monitor);
//					getWorkbench().getDisplay().asyncExec(new Runnable() {
//
//						@Override
//						public void run() {
//							try {
//								IWorkbenchPage activePage = getWorkbench().getActiveWorkbenchWindow().getActivePage();
//								// IDE.openEditor(activePage, generator.getSourceFile());
//							}
//							catch(PartInitException e) {
//								//Activator.getDefault().getLog().log(e.getStatus());
//							}
//						}
//							
//						
//					});
					return Status.OK_STATUS;
				}
				catch(CoreException e) {
					return e.getStatus();
				}
			}
		
			
			
		}.schedule();
		return true;
	}
}
