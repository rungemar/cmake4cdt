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

import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuildCommandParser;

/**
 * @author runge_m
 *
 */
public class CMakeCompileCommandParserGCC extends GCCBuildCommandParser implements IBuildCommandParserEx {

	public CompileUnitInfo getCompileUnitInfo() {
		return new CompileUnitInfo(parsedResourceName, currentResource);
	}
	
	public void shutdown() {
		super.shutdown();
	}
	
		
}
