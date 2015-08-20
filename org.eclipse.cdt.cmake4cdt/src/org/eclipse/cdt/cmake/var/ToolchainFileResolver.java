/*******************************************************************************
 * Copyright (c) 2014, 2015 Rohde & Schwarz GmbH & Co. KG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Runge - initial implementation of cmake support
 *******************************************************************************/
package org.eclipse.cdt.cmake.var;


import org.eclipse.cdt.cmake.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin; 

public class ToolchainFileResolver implements IDynamicVariableResolver {

	@Override
	public String resolveValue(IDynamicVariable variable, String argument)	throws CoreException {

		ArchToolchainPairList atlist = new ArchToolchainPairList(); 
		atlist.doLoad();
		String toolchainFile = atlist.get(argument);
		
		if(toolchainFile == null) {
			Status status = new Status(Status.INFO, Activator.PLUGIN_ID, "No CMake toolchainfile specified for architecture '" + argument +"'");
			throw new CoreException(status);
		}

		IStringVariableManager varMgr = VariablesPlugin.getDefault().getStringVariableManager();
		toolchainFile = varMgr.performStringSubstitution(toolchainFile);
		return toolchainFile;
	}

}
