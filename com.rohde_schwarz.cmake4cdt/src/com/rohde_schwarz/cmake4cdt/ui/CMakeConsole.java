package com.rohde_schwarz.cmake4cdt.ui;

import org.eclipse.cdt.core.ConsoleOutputStream;
import org.eclipse.cdt.core.resources.IConsole;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.cdt.ui.IBuildConsoleManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class CMakeConsole implements IConsole {
	IProject project;
	IBuildConsoleManager fConsoleManager;

	public CMakeConsole() {
		fConsoleManager = CUIPlugin.getDefault().getConsoleManager("CMake console", "com.rohde_schwarz.cmake4cdt.CMakeConsole");
	}

	@Override
	public void start(IProject project) {
		this.project = project;
		fConsoleManager.getConsole(project).start(project);
	}

	@Override
	public ConsoleOutputStream getOutputStream() throws CoreException {
		return fConsoleManager.getConsole(project).getOutputStream();
	}

	@Override
	public ConsoleOutputStream getInfoStream() throws CoreException {
		return fConsoleManager.getConsole(project).getInfoStream();
	}

	@Override
	public ConsoleOutputStream getErrorStream() throws CoreException {
		return fConsoleManager.getConsole(project).getErrorStream();
	}

}
