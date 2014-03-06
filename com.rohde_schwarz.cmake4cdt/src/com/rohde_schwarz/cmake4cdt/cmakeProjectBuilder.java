package com.rohde_schwarz.cmake4cdt;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class cmakeProjectBuilder extends IncrementalProjectBuilder {

	public static String BUILDER_ID="com.rohde_schwarz.cmake4cdt.cmakeProjectBuilder";
	
	public cmakeProjectBuilder() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
}
