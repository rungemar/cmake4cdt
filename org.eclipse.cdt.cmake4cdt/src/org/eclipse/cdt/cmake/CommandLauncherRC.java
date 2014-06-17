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

import java.io.OutputStream;

import org.eclipse.cdt.core.CommandLauncher;
import org.eclipse.cdt.core.ICommandLauncher;
import org.eclipse.cdt.internal.core.Messages;
import org.eclipse.cdt.internal.core.ProcessClosure;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author runge_m
 *
 */
public class CommandLauncherRC extends CommandLauncher implements ICommandLauncher {

	public int exitValue() {
		return fProcess.exitValue();
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.cdt.core.ICommandLauncher#waitAndRead(java.io.OutputStream, java.io.OutputStream, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public int waitAndRead(OutputStream output, OutputStream err, IProgressMonitor monitor) {
		if (fShowCommand) {
			printCommandLine(output);
		}

		if (fProcess == null) {
			return ICommandLauncher.ILLEGAL_COMMAND;
		}

		ProcessClosure closure = new ProcessClosure(fProcess, output, err);
		closure.runNonBlocking();
		while (!monitor.isCanceled() && closure.isAlive()) {
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException ie) {
				// ignore
			}
		}

		int state = ICommandLauncher.OK;

		// Operation canceled by the user, terminate abnormally.
		if (monitor.isCanceled()) {
			closure.terminate();
			state = ICommandLauncher.COMMAND_CANCELED;
			setErrorMessage(Messages.CommandLauncher_CommandCancelled);
		}

		try {
			state = fProcess.waitFor();
		} catch (InterruptedException e) {
			// ignore
		}
		return state;
	}

}
