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

import java.io.ByteArrayOutputStream;

import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * @author runge_m
 *
 */
public class CMakeInfoRetriever {

	private final IAcceptsCMakeInfo receiver;
	private CMakeInfo cmakeInfoData = null;
	private Job tryExecJob= null;

	/**
	 * @param receiver
	 */
	public CMakeInfoRetriever(IAcceptsCMakeInfo receiver) {
		super();
		this.receiver = receiver;
	}
	
	public boolean tryExecCMake(final IPath p) {
		boolean success = false;
		
		this.cmakeInfoData = new CMakeInfo();
		
		tryExecJob = new Job("trying to execute CMake") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ICommandLauncher cl = new CommandLauncher();
				IStatus status = new Status(IStatus.WARNING, Activator.getId(), "OK");
				try {
					
					String[] args = new String[1];
					args[0] = "--help";
					cl.execute(p, args, null,  null, monitor);
					
					ByteArrayOutputStream stdout = new ByteArrayOutputStream();
					ByteArrayOutputStream stderr = new ByteArrayOutputStream();
	
					if( cl.waitAndRead(stdout, stderr, monitor) != CommandLauncher.OK) {
						status = new Status(IStatus.ERROR, Activator.getId(), cl.getErrorMessage());
					}
					
					cmakeInfoData.parseStdOut(stdout.toString());
					cmakeInfoData.parseStdErr(stderr.toString());

				} catch (CoreException e) {
					status = new Status(IStatus.ERROR, Activator.getId(), "could not execute '" + p.toOSString() + "'" );
				}
				
				
				Display.getDefault().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						receiver.setCMakeInfo(cmakeInfoData);
					}
				});				

				return status;
			}
			
		};
		
		tryExecJob.schedule();
		return success;
	}
	
	public void cancelTryExecCMake() {
		if(tryExecJob != null) {
			tryExecJob.cancel();
		}
	}

	
	
}
