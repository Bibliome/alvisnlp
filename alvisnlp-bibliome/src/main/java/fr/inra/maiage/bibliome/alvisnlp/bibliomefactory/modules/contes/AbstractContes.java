package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.ExecutableFile;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

public interface AbstractContes {
	@Param
	InputDirectory getContesDir();

	@Deprecated
	@Param(nameType=NameType.LAYER)
	String getTokenLayerName();
	
	@Param(nameType=NameType.LAYER)
	String getTokenLayer();

	@Deprecated
	@Param(nameType=NameType.FEATURE)
	String getFormFeatureName();
	
	@Param(nameType=NameType.FEATURE)
	String getFormFeature();

	@Param
	ExecutableFile getPython3Executable();

	@Param(mandatory=false)
	String[] getAdditionalArguments();
	
	void setAdditionalArguments(String[] additionalArguments);
	
	void setContesDir(InputDirectory contesDir);

	@Deprecated
	void setTokenLayerName(String tokenLayer);

	void setTokenLayer(String tokenLayer);

	@Deprecated
	void setFormFeatureName(String formFeature);

	void setFormFeature(String formFeature);

	void setPython3Executable(ExecutableFile python3Executable);
}
