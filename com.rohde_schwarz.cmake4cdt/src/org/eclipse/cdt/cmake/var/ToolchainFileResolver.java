package org.eclipse.cdt.cmake.var;


import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.cdt.cmake.Activator;
import org.eclipse.cdt.cmake.ui.PreferenceConstants; 

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
