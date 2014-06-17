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
package org.eclipse.cdt.cmake.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.cmake.CMakeMakefileGenerator;
import org.eclipse.cdt.cmake.CMakeProjectBuilderImpl;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class RunCMakeAction implements IObjectActionDelegate {

	private Shell shell;
	ArrayList<IContainer> fContainer;
	
	/**
	 * @return the fContainer
	 */
	public IContainer[] getSelectedContainer(int i) {
		return (IContainer[]) fContainer.toArray();
	}

	/**
	 * Constructor for Action1.
	 */
	public RunCMakeAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		//IContainer container = getSelectedContainer();
		//if (container == null)
        //    return;

		// We need to use a workspace root scheduling rule because adding MakeTargets
		// may end up saving the project description which runs under a workspace root rule.
		final ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRoot();
		
		Job backgroundJob = new Job("CMake Action"){  //$NON-NLS-1$
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

						public void run(IProgressMonitor monitor) throws CoreException {
							for(int i=0; i < fContainer.size(); i++) {
								IProject project = fContainer.get(i).getProject();
								IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
								CMakeProjectBuilderImpl pb = new CMakeProjectBuilderImpl(project, info);
								CUIPlugin.getDefault().startGlobalConsole();
								// m.initialize(project, info, monitor);
								try {
									pb.build(IncrementalProjectBuilder.FULL_BUILD, null, monitor);
								} catch (CoreException e) {
									// Try to inform user that CMake failed: Attention wrong thread for Dialogs here!
								}
							}
						}
					}, rule, IWorkspace.AVOID_UPDATE, monitor);
				} catch (CoreException e) {
					return e.getStatus();
				}
				IStatus returnStatus = Status.OK_STATUS;
				return returnStatus;
			}
		};

		backgroundJob.setRule(rule);
		backgroundJob.schedule();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		fContainer = new ArrayList<IContainer>();
		boolean enabled = false;
		if (selection instanceof IStructuredSelection) 
		{
			IStructuredSelection sel = (IStructuredSelection) selection;
			for (Iterator<Object> iterator = sel.iterator(); iterator.hasNext();) 
			{
				Object obj = iterator.next();
				if (obj instanceof ICElement) {
					if ( obj instanceof ICContainer || obj instanceof ICProject) {
						fContainer.add((IContainer) ((ICElement) obj).getUnderlyingResource());
					} else {
						obj = ((ICElement)obj).getResource();
						if ( obj != null) {
							fContainer.add(((IResource)obj).getParent());
						}
					}
				} else if (obj instanceof IResource) {
					if (obj instanceof IContainer) {
						fContainer.add((IContainer) obj);
					} else {
						fContainer.add(((IResource)obj).getParent());
					}
				} else {
				}
			}
			if (!fContainer.isEmpty()) {
				enabled = true;
			}
		}
		action.setEnabled(enabled);
	}
}
