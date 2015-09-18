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

package org.eclipse.cdt.cmake.langset;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.cdt.core.language.settings.providers.IWorkingDirectoryTracker;

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