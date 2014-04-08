package com.rohde_schwarz.cmake4cdt.ui;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import com.rohde_schwarz.cmake4cdt.Activator;
import com.rohde_schwarz.cmake4cdt.ui.CMakePropertyConstants;
import com.rohde_schwarz.cmake4cdt.ui.PreferenceConstants;

public class CMakePropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	//	private final IResource resource;
//	private final IProject activeProject;
//	private final IEclipsePreferences projectProperties;
	private Composite buildDirEntry;
	
	private Button deviceSpecificBtn;
	private Button useWorkspaceSettings;
	private Text buildDirTextField;
    private File filterPath = null;


	public CMakePropertyPage() {
//		IResource resource = (IResource) getElement();
//		IProject activeProject = resource.getProject();
//		IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite myComposite = new Composite(parent, SWT.NONE);
		GridLayout mylayout = new GridLayout();
		mylayout.marginHeight = 1;
		mylayout.marginWidth = 1;
		myComposite.setLayout(mylayout);

		deviceSpecificBtn = new Button(myComposite, SWT.CHECK);
		deviceSpecificBtn.setLayoutData(new GridData());
		deviceSpecificBtn.setText("Project is instrument specific");
		deviceSpecificBtn.setSelection( getIsInstrumentSpecific() );

		Label buildDirLabel = new Label(myComposite, SWT.NONE);
		buildDirLabel.setLayoutData(new GridData());
		buildDirLabel.setText("Build in:");

		useWorkspaceSettings = new Button(myComposite, SWT.CHECK);
		useWorkspaceSettings.setLayoutData(new GridData());
		useWorkspaceSettings.setText("Use Workspace settings for build dir");
		useWorkspaceSettings.setSelection( getIsWorkspaceSpecific() );
		useWorkspaceSettings.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  setIsWorkspaceSpecific( ((Button) event.getSource()).getSelection()  );
		      }
		});
		
		buildDirEntry = new Composite(myComposite, SWT.NONE);
		
		GridLayout bdlayout = new GridLayout(3, false);
		bdlayout.marginHeight = 1;
		bdlayout.marginWidth = 1;
		buildDirEntry.setLayout(bdlayout);

		buildDirTextField = new Text(buildDirEntry, SWT.BORDER);
		buildDirTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buildDirTextField.setText( getProjectSpecificBuildDir() );

		Button browseBtn = new Button(buildDirEntry, SWT.PUSH);
		browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		browseBtn.setText("Browse");
		browseBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                String newValue = browsePressed();
                if (newValue != null) {
                	buildDirTextField.setText(newValue);
                }
            }
        });

		Button varsBtn = new Button(buildDirEntry, SWT.PUSH);
		varsBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		varsBtn.setText("Variables");
		varsBtn.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent evt) {
                String newValue = variablesPressed();
                if (newValue != null) {
                	buildDirTextField.setText(newValue);
                }
            }
        });


		buildDirEntry.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setIsWorkspaceSpecific( getIsWorkspaceSpecific() );

		return myComposite;
	}
	
	private boolean getIsWorkspaceSpecific() {
		IResource resource = (IResource) getElement();
		IProject activeProject = resource.getProject();
		IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
		boolean value = false;
		if (projectProperties != null) {
			value = projectProperties.getBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, false);
		}
		return value;
	}

	private void setIsWorkspaceSpecific(boolean isSpecific) {
		useWorkspaceSettings.setSelection(isSpecific);
		buildDirEntry.setEnabled(!isSpecific);
		for (Control child : buildDirEntry.getChildren()) {
			  child.setEnabled(!isSpecific);
		}
	}

	protected boolean getIsInstrumentSpecific() {
		IResource resource = (IResource) getElement();
		IProject activeProject = resource.getProject();
		IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
		boolean value = false;
		if (projectProperties != null) {
			value = projectProperties.getBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, false);
		}
		return value;
	}
	
	protected String getProjectSpecificBuildDir() {
		IResource resource = (IResource) getElement();
		IProject activeProject = resource.getProject();
		IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");
		String buildDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);
		if (projectProperties != null) {
			buildDirStr = projectProperties.get(CMakePropertyConstants.P_BUILD_PATH, buildDirStr);
		}
		return buildDirStr;
	}
	
	public String browsePressed() {
		File f = new File(buildDirTextField.getText());
		if (!f.exists()) {
			f = null;
		}
		File d = getDirectory(f);
		if (d == null) {
			return null;
		}

		return d.getAbsolutePath();
	}

	public String variablesPressed() {
		StringVariableSelectionDialog varDlg = new StringVariableSelectionDialog(getShell());
		varDlg.open();
		String variableExpression = varDlg.getVariableExpression();

		String currentDestdir = buildDirTextField.getText();

		if(variableExpression == null) {
			return currentDestdir;
		}

		Point pt = buildDirTextField.getSelection();

		String result = currentDestdir.substring(0, pt.x);
		result = result.concat(variableExpression);
		result = result.concat(currentDestdir.substring(pt.y, currentDestdir.length()));

		return result;
	}

    /**
     * Helper that opens the directory chooser dialog.
     * @param startingDirectory The directory the dialog will open in.
     * @return File File or <code>null</code>.
     * 
     */
    private File getDirectory(File startingDirectory) {

        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
        if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}
        else if (filterPath != null) {
        	fileDialog.setFilterPath(filterPath.getPath());
        }
        String dir = fileDialog.open();
        if (dir != null) {
            dir = dir.trim();
            if (dir.length() > 0) {
				return new File(dir);
			}
        }

        return null;
    }


	
	public void performApply() {
		IResource resource = (IResource) getElement();
		IProject activeProject = resource.getProject();
		IEclipsePreferences projectProperties = new ProjectScope(activeProject).getNode("com.rohde_schwarz.buildif.scope");

		if (projectProperties != null) {
			try {
				projectProperties.putBoolean(CMakePropertyConstants.P_IS_DEVICE_SPECIFIC, deviceSpecificBtn.getSelection());
				projectProperties.putBoolean(CMakePropertyConstants.P_USE_WORKSPACE_BUILDDIR_SETTINGS, useWorkspaceSettings.getSelection());
				if(!getIsWorkspaceSpecific()) {
					// build dir is project specific
					projectProperties.put(CMakePropertyConstants.P_BUILD_PATH, buildDirTextField.getText());
				}
				projectProperties.flush();
			}
			catch(BackingStoreException beex) {
			}
		}
	
	}

	public void performDefaults() {
		deviceSpecificBtn.setSelection(false);
		setIsWorkspaceSpecific(true);
		String buildDirStr = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUILDDIR);
		buildDirTextField.setText( buildDirStr );
	}

	public boolean performOk() {
		performApply();
		return true;
	}
	
	public boolean performCancel() {
		System.out.println("performCancel");
		return true;
	}


}
