//package org.eclipse.cdt.cmake.langset;
//
//import java.util.List;
//import java.util.Map;
//
//import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsBaseProvider;
//import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
//import org.eclipse.cdt.core.settings.model.ICLanguageSettingEntry;
//import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuiltinSpecsDetector;
//import org.eclipse.cdt.managedbuilder.language.settings.providers.ToolchainBuiltinSpecsDetector;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
//
//public class CMakeBuiltInSettingsProvider extends LanguageSettingsBaseProvider implements ILanguageSettingsProvider {
//
//	//ToolchainBuiltinSpecsDetector m_builtinSpecsDetector = null;
//	CMakeGCCBuiltinSpecsDetector m_builtinSpecsDetector = null;
//	
//	public CMakeBuiltInSettingsProvider() {
//	}
//
//	public CMakeBuiltInSettingsProvider(String id, String name) {
//		super(id, name);
//	}
//
//	public CMakeBuiltInSettingsProvider(String id, String name, List<String> languages,
//			List<ICLanguageSettingEntry> entries) {
//		super(id, name, languages, entries);
//	}
//
//	public CMakeBuiltInSettingsProvider(String id, String name, List<String> languages,
//			List<ICLanguageSettingEntry> entries, Map<String, String> properties) {
//		super(id, name, languages, entries, properties);
//	}
//	
//	public List<ICLanguageSettingEntry> getSettingEntries(ICConfigurationDescription cfgDescription, IResource rc, String languageId) {
//		if(m_builtinSpecsDetector == null) {
//			try {
//				m_builtinSpecsDetector = new CMakeGCCBuiltinSpecsDetector();
//				m_builtinSpecsDetector.setCommand("g++");
//				m_builtinSpecsDetector.startup(cfgDescription, null);
//				m_builtinSpecsDetector.scan();
//				m_builtinSpecsDetector.shutdown();
//			} catch (CoreException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}
//
//}
