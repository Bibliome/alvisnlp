package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.contes;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.files.InputDirectory;

public interface AbstractContes {
	@Param
	public InputDirectory getContesDir();

	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName();
	
	@Param(nameType=NameType.FEATURE)
	public String getFormFeatureName();

	public void setContesDir(InputDirectory contesDir);

	public void setTokenLayerName(String tokenLayer);

	public void setFormFeatureName(String formFeature);
}
