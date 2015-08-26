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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.resources.ACBuilder;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;

public class CMakeProjectBuilderImpl extends ACBuilder {

	public static String BUILDER_ID="org.eclipse.cdt.cmake.CMakeProjectBuilder";
	public final String CMAKE_EXE = "cmake";
	
	private IProject privateProject = null;
	private IManagedBuildInfo privateBuildInfo = null;
	private CMakeMakefileGenerator mfgen = null;	
	
	public CMakeProjectBuilderImpl() {
		super();
		mfgen = new CMakeMakefileGenerator();
	}
	
	public CMakeProjectBuilderImpl( IProject project, IManagedBuildInfo info ) {
		super();
		privateProject = project;
		privateBuildInfo = info;
		mfgen = new CMakeMakefileGenerator();
	}

	@Override
	public IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {

		IProject project;
		IManagedBuildInfo buildInfo;
		if(privateProject == null) {
			project = getProject();
		} else {
			project = privateProject;
		}
	
		if(privateBuildInfo == null) {
			buildInfo = ManagedBuildManager.getBuildInfo(project);
		} else {
			buildInfo = privateBuildInfo;
		}

		IConsole cmakeConsole = CCorePlugin.getDefault().getConsole("org.eclipse.cdt.cmake.ui.CMakeConsole"); //$NON-NLS-1$
		cmakeConsole.start(project);

		MultiStatus mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", 0, "success", null );

		try {
			mfgen.initialize(project, buildInfo, monitor);
			IConfiguration activeConfig = buildInfo.getDefaultConfiguration();
	
			// String currentConf = activeConfig.getName();
			// String currentArch = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_ARCH);
			// String currentInstrument = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_TARGET_DEVICE);
		
			IPath buildDir = mfgen.getBuildWorkingDir();
			activeConfig.getEditableBuilder().setBuildPath(buildDir.toString());
			// ManagedBuildManager.saveBuildInfo(project, true);
			mstatus = mfgen.runCMake();
		}
		catch(CoreException ce) {
			logToConsole(cmakeConsole, ce.getStatus());
			mstatus = new MultiStatus("org.eclipse.cdt.cmake.builder", 1, ce.getStatus().getMessage(), null );
		}
		finally {
			if(mstatus.getCode() != 0) {
				throw new OperationCanceledException(mstatus.getMessage());
			}
		}
		
		return project.getReferencedProjects();
	}
	
	
	protected boolean shouldBuild(int kind, IManagedBuildInfo info) {
		IConfiguration cfg = info.getDefaultConfiguration();
		IBuilder builder = null;
		if (cfg != null) {
			builder = cfg.getEditableBuilder();
		switch (kind) {
		case IncrementalProjectBuilder.AUTO_BUILD :
			return true;
		case IncrementalProjectBuilder.INCREMENTAL_BUILD : // now treated as the same!
		case IncrementalProjectBuilder.FULL_BUILD :
			return builder.isFullBuildEnabled() | builder.isIncrementalBuildEnabled() ;
		case IncrementalProjectBuilder.CLEAN_BUILD :
			return builder.isCleanBuildEnabled();
		}
		}
		return true;
	}
	
	private void logToConsole(IConsole console, IStatus status) {
		String errmsg = new String();
		OutputStream cos;
		try {
			cos = console.getOutputStream();
			StringBuffer buf = new StringBuffer(errmsg);
			buf.append(System.getProperty("line.separator", "\n")); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append(status.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$

			try {
				cos.write(buf.toString().getBytes());
				cos.flush();
				cos.close();
			} catch (IOException e) {
				ResourcesPlugin.getPlugin().getLog().log(status);
			}

		} catch (CoreException e1) {
			e1.printStackTrace();
		}
	}
}
