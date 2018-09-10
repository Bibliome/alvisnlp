package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

public interface AbstractContes {
	@Param
	InputDirectory getContesDir();

	@Param(nameType=NameType.LAYER)
	String getTokenLayerName();
	
	@Param(nameType=NameType.FEATURE)
	String getFormFeatureName();
	
	@Param
	ExecutableFile getPython3Executable();

	void setContesDir(InputDirectory contesDir);

	void setTokenLayerName(String tokenLayer);

	void setFormFeatureName(String formFeature);
	
	void setPython3Executable(ExecutableFile python3Executable);
}
