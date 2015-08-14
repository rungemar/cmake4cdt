package org.eclipse.cdt.cmake.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.cmake.CMakeMakefileGenerator;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.ui.newui.AbstractCPropertyTab;
import org.eclipse.cdt.ui.newui.ICPropertyProvider;
import org.eclipse.cdt.ui.newui.ICPropertyTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Shell;

public class CPropertyTab extends AbstractCPropertyTab {


	public final static String CMAKE_OPTION_TOOLCHAINFILE = "org.eclipse.cdt.cmake.option.toolchainFile";
	public final static String CMAKE_OPTION_BUILDTYPE = "org.eclipse.cdt.cmake.option.buildType";
	public final static String CMAKE_OPTION_DEBUG = "org.eclipse.cdt.cmake.option.debug";
	public final static String CMAKE_OPTION_TRACE = "org.eclipse.cdt.cmake.option.trace";
	

	private Combo cmakeBuildTypeCombo;
	private Text  cmakeToolchainFileTextField;
	private Button traceBtn;
	private Button debugBtn;
	
	private ICConfigurationDescription cfgd = null;
	private String  m_toolchainFile = null;
	private String  m_buildType = null;
	private boolean m_trace = false;
	private boolean m_debug = false;

	public CPropertyTab() {
	}

	@Override
	public void createControls(Composite parent, ICPropertyProvider provider) {
		super.createControls(parent);
		page = provider;
	
		usercomp.setLayout(new GridLayout(3, false));
		Label l1 = new Label(usercomp, SWT.LEFT);
		l1.setText("Settings and arguments for the call to cmake");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		l1.setLayoutData(gd);
		

		Label cmakeBuildTypeLabel = new Label(usercomp, SWT.NONE);
		cmakeBuildTypeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		cmakeBuildTypeLabel.setText("Build type (-DCMAKE_BUILD_TYPE):");
		
		String[] buildTypes = {"Debug", "Release", "RelWithDebInfo", "MinSizeRel"};
		cmakeBuildTypeCombo = new Combo(usercomp, SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		cmakeBuildTypeCombo.setLayoutData(gd);
		cmakeBuildTypeCombo.setItems(buildTypes);
		cmakeBuildTypeCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_buildType = cmakeBuildTypeCombo.getText();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		

		Label cmakeToolchainFileLabel = new Label(usercomp, SWT.NONE);
		cmakeToolchainFileLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		cmakeToolchainFileLabel.setText("Toolchain (-DCMAKE_TOOLCHAIN_FILE):");
		
		cmakeToolchainFileTextField = new Text(usercomp, SWT.BORDER);
		cmakeToolchainFileTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cmakeToolchainFileTextField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				m_toolchainFile = cmakeToolchainFileTextField.getText();
			}
		});
		
		Button browseBtn = new Button(usercomp, SWT.PUSH);
		browseBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		browseBtn.setText("Browse");
		browseBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				String newValue = browsePressed();
				if (newValue != null) {
					cmakeToolchainFileTextField.setText(newValue);
				}
			}
		});


		Label cmakeTraceLabel = new Label(usercomp, SWT.NONE);
		cmakeTraceLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		cmakeTraceLabel.setText("Trace mode (--trace):");
		
		traceBtn = new Button(usercomp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		
		traceBtn.setLayoutData(gd);
		// traceBtn.setText("trace cmake run");
		traceBtn.setSelection( false );
		traceBtn.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  m_trace = ((Button) event.getSource()).getSelection();
		      }
		});
		
		
		Label cmakeDebugLabel = new Label(usercomp, SWT.NONE);
		cmakeDebugLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		cmakeDebugLabel.setText("Debug mode (--debug-output):");
		
		debugBtn = new Button(usercomp, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		
		debugBtn.setLayoutData(gd);
		// debugBtn.setText("");
		debugBtn.setSelection( false );
		debugBtn.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  m_debug = ((Button) event.getSource()).getSelection();
		      }
		});
		
		ICResourceDescription _cfgd = getResDesc();
		cfgd = (_cfgd != null) ? _cfgd.getConfiguration() : null;
		// getToolSettings();
		
		return;
	}

	@Override
	public void handleTabEvent(int kind, Object data) {
		super.handleTabEvent(kind, data);

	}

	@Override
	public boolean canBeVisible() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.newui.AbstractCPropertyTab#performApply(org.eclipse.cdt.core.settings.model.ICResourceDescription, org.eclipse.cdt.core.settings.model.ICResourceDescription)
	 */
	@Override
	protected void performApply(ICResourceDescription src, ICResourceDescription dst) {
		setToolSettings();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.newui.AbstractCPropertyTab#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		
		
	}
	
	protected void performOK() {
		setToolSettings();
		super.performOK();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.newui.AbstractCPropertyTab#updateData(org.eclipse.cdt.core.settings.model.ICResourceDescription)
	 */
	@Override
	protected void updateData(ICResourceDescription _cfgd) {
		// null means preference configuration
		cfgd = (_cfgd != null) ? _cfgd.getConfiguration() : null;
		if (cfgd != null) {
			getToolSettings();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.cdt.ui.newui.AbstractCPropertyTab#updateButtons()
	 */
	@Override
	protected void updateButtons() {
		// TODO Auto-generated method stub
		
	}
	
	private void getToolSettings()   {
		ITool cmakeTool = getCMakeTool();

		try {
			IOption toolchainfileOption = cmakeTool.getOptionBySuperClassId(CMAKE_OPTION_TOOLCHAINFILE);
			m_toolchainFile = toolchainfileOption.getStringValue();
			cmakeToolchainFileTextField.setText(m_toolchainFile);

			IOption builyTypeOption = cmakeTool.getOptionBySuperClassId(CMAKE_OPTION_BUILDTYPE);
			m_buildType = builyTypeOption.getStringValue();
			switch(m_buildType) 
			{
			case "Debug":          cmakeBuildTypeCombo.select(0);
			case "Release":        cmakeBuildTypeCombo.select(1);
			case "RelWithDebInfo": cmakeBuildTypeCombo.select(2);
			case "MinSizeRel":     cmakeBuildTypeCombo.select(3);
			default:               cmakeBuildTypeCombo.select(0);
			}					
			
			IOption debugOption = cmakeTool.getOptionBySuperClassId(CMAKE_OPTION_DEBUG);
			m_debug = debugOption.getBooleanValue();
			debugBtn.setSelection(m_debug);
			
			IOption traceOption = cmakeTool.getOptionBySuperClassId(CMAKE_OPTION_TRACE);
			m_trace = traceOption.getBooleanValue();
			traceBtn.setSelection(m_trace);
			
		} catch (BuildException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void setToolSettings()  {
		ITool cmakeTool = getCMakeTool();
		
		try {
	        // -- get resource info. (where things are saved to).
	        IResourceInfo resourceInfos[] = ManagedBuildManager.getConfigurationForDescription(cfgd).getResourceInfos();
	        IResourceInfo resourceInfo = resourceInfos[0];

	        IOption toolchainfileOptionTmpl = cmakeTool.getOptionById(CMAKE_OPTION_TOOLCHAINFILE);

	        IOption toolchainfileOption = cmakeTool.getOptionToSet(toolchainfileOptionTmpl, false);
			toolchainfileOption.setValue(m_toolchainFile);
	        ManagedBuildManager.setOption(resourceInfo, cmakeTool, toolchainfileOption, m_toolchainFile);

			IOption builyTypeOptionTmpl = cmakeTool.getOptionById(CMAKE_OPTION_BUILDTYPE);
			IOption builyTypeOption = cmakeTool.getOptionToSet(builyTypeOptionTmpl, false);
			builyTypeOption.setValue(m_buildType);
	        ManagedBuildManager.setOption(resourceInfo, cmakeTool, toolchainfileOption, m_toolchainFile);

			IOption debugOptionTmpl = cmakeTool.getOptionById(CMAKE_OPTION_DEBUG);
			IOption debugOption = cmakeTool.getOptionToSet(debugOptionTmpl, false);
			debugOption.setValue(m_debug);
	        ManagedBuildManager.setOption(resourceInfo, cmakeTool, toolchainfileOption, m_toolchainFile);
			
			IOption traceOptionTmpl = cmakeTool.getOptionById(CMAKE_OPTION_TRACE);
			IOption traceOption = cmakeTool.getOptionToSet(traceOptionTmpl, false);
			traceOption.setValue(m_trace);
	        ManagedBuildManager.setOption(resourceInfo, cmakeTool, toolchainfileOption, m_toolchainFile);
 
	        // ------ Save this business to disk.
	        ManagedBuildManager.saveBuildInfo(cfgd.getProjectDescription().getProject(), true);
			
		} catch (BuildException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	private ITool getCMakeTool() {
		IConfiguration cfg = ManagedBuildManager.getConfigurationForDescription(cfgd);
		ITool[] cmakeTools = cfg.getToolsBySuperClassId(CMakeMakefileGenerator.CMAKE_TOOL_ID);
		ITool cmakeTool = null;
		if(cmakeTools.length > 0) {
			cmakeTool = cmakeTools[0];
		}
		return cmakeTool;
	}
	
	private String browsePressed() {
		File f = new File(cmakeToolchainFileTextField.getText());
		if (!f.exists()) {
			f = null;
		}
		File d = getFile(f);
		if (d == null) {
			return null;
		}

		return d.getAbsolutePath();
	}
	
	private File getFile(File startingDirectory) {

		FileDialog dialog = new FileDialog(usercomp.getShell(), SWT.OPEN);

		if (startingDirectory != null) {
			dialog.setFilterPath(startingDirectory.getPath());
		}
//		else if (filterPath != null) {
//			dialog.setFilterPath(filterPath.getPath());
//		}
	   String result = dialog.open();
		   
		   
	   
		if (result != null) {
			result = result.trim();
			return new File(result);
		}

		return null;
	}




}
