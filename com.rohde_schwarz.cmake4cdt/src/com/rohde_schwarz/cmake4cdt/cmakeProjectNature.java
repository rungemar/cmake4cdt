package com.rohde_schwarz.cmake4cdt;

import org.eclipse.cdt.managedbuilder.core.ManagedBuilderCorePlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public class cmakeProjectNature implements IProjectNature {

	public static final String CMAKE_NATURE_ID = ManagedBuilderCorePlugin.getUniqueIdentifier() + ".cmakeNature";  //$NON-NLS-1$
	private IProject project;
	
	public cmakeProjectNature() {
		
	}

	@Override
	public void configure() throws CoreException {
		addNature(this.project, new NullProgressMonitor());
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
}
