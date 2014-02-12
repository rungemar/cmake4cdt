package com.rohde_schwarz.cmake4cdt;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.ManagedBuilderCorePlugin;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

public class cmakeProjectNature implements IProjectNature {

	public static final String CMAKE_NATURE_ID = "com.rohde_schwarz.cmake4cdt.cmakeNature";  //$NON-NLS-1$
	public final static String BUILDER_ID = ManagedBuilderCorePlugin.getUniqueIdentifier() + ".genmakebuilder"; //$NON-NLS-1$

	private IProject project;
	
	public cmakeProjectNature() {
		
	}

	@Override
	public void configure() throws CoreException {
		addNature(this.project, new NullProgressMonitor());
		addBuilder(this.project, new NullProgressMonitor());
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IProject getProject() {
		return this.project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static void addNature(IProject project, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description;
		description = project.getDescription();
		String[] prevNatures = description.getNatureIds();
		for (int i = 0; i < prevNatures.length; i++) {
			if (CMAKE_NATURE_ID.equals(prevNatures[i]))
				return;
		}
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = CMAKE_NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, monitor);
	}
	
	public static void addBuilder(IProject project, IProgressMonitor monitor) throws CoreException {
		// Add the builder to the project
		IProjectDescription description = project.getDescription();
		ICommand[] commands = description.getBuildSpec();
		if(checkEquals(commands,getBuildCommandsList(description, commands))){
			return;
		}
		final ISchedulingRule rule = ResourcesPlugin.getWorkspace().getRoot();
		final IProject proj = project;

		Job backgroundJob = new Job("CMake Set Project Description") {
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
						protected boolean savedAutoBuildingValue;

						public void run(IProgressMonitor monitor) throws CoreException {
							IWorkspace workspace = ResourcesPlugin.getWorkspace();
							turnOffAutoBuild(workspace);
							IProjectDescription prDescription = proj.getDescription();
							//Other pieces of wizard might have contributed new builder commands;
							//need to make sure we are using the most recent ones
							ICommand[] currentCommands = prDescription.getBuildSpec();
							ICommand[] newCommands = getBuildCommandsList(prDescription, currentCommands); 
							if(!checkEquals(currentCommands,newCommands)){
								prDescription.setBuildSpec(newCommands);
								proj.setDescription(prDescription, new NullProgressMonitor());
							}
							restoreAutoBuild(workspace);
						}
						
						protected final void turnOffAutoBuild(IWorkspace workspace) throws CoreException {
							IWorkspaceDescription workspaceDesc = workspace.getDescription();
							savedAutoBuildingValue = workspaceDesc.isAutoBuilding();
							workspaceDesc.setAutoBuilding(false);
							workspace.setDescription(workspaceDesc);
						}
						
						protected final void restoreAutoBuild(IWorkspace workspace) throws CoreException {
							IWorkspaceDescription workspaceDesc = workspace.getDescription();
							workspaceDesc.setAutoBuilding(savedAutoBuildingValue);
							workspace.setDescription(workspaceDesc);
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
	
	static boolean checkEquals(ICommand[] commands, ICommand[] newCommands) {
		if (newCommands.length != commands.length){
			return false;
		}
		for (int j = 0; j < commands.length; ++j) {
			if (!commands[j].getBuilderName().equals(newCommands[j].getBuilderName())) {
				return false;
			}
		}
		return true;
	}

	static ICommand[] getBuildCommandsList(IProjectDescription description,	ICommand[] commands) {
		ArrayList<ICommand> commandList = new ArrayList<ICommand>();

		// Make sure the CMake builder just precedes the Common Builder
		for (int i = 0; i < commands.length; i++) {
			ICommand command = commands[i];
			if (command.getBuilderName().equals(cmakeProjectBuilder.BUILDER_ID)) {
				// ignore it
			} else {
				if (command.getBuilderName().equals(BUILDER_ID)) {
					// add CMake Configuration builder just before builder
					ICommand newCommand = description.newCommand();
					newCommand.setBuilderName(cmakeProjectBuilder.BUILDER_ID);
					commandList.add(newCommand);
				}
				commandList.add(command);
			}
		}
		return commandList.toArray(new ICommand[commandList.size()]);
	}

	
}
