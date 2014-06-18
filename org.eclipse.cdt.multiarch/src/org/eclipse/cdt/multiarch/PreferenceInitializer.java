package org.eclipse.cdt.multiarch;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.cdt.multiarch.Activator;
import org.eclipse.cdt.multiarch.PreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_AVAIL_ARCHS, "arm;ppc;x86;native");
		store.setDefault(PreferenceConstants.P_AVAIL_INSTRUMETS, "sgs;sgu;sma;smb;smbv;smc;smf;smw");
		store.setDefault(PreferenceConstants.P_NOT_INST_SPECIFIC, "");

		store.setDefault(PreferenceConstants.P_BUILDDIR, "${BuildIF_ProjectPath}/${ConfigName}_${BuildIF_Arch}");
		store.setDefault(PreferenceConstants.P_DESTDIR, "${env_var:HOME}/${BuildIF_Instrument}/${ConfigName}_${BuildIF_Arch}/opt/");

	}

}
