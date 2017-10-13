package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.chemspot;

import java.util.List;

import fr.inra.maiage.bibliome.util.filelines.FileLines;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;

public abstract class ChemspotFileLines<D,A> extends FileLines<D> {

	@Override
	public void processEntry(D data, int lineno, List<String> entry) throws InvalidFileLineEntry {
		int begin = Integer.parseInt(entry.get(1));
		if (begin == -1) {
			return;
		}
		int end = Integer.parseInt(entry.get(2));
		String form = entry.get(3);
		A annotation = createAnnotation(data, begin, end, form);
		if (annotation == null) {
			return;
		}
		setType(annotation, entry.get(4));
		setCHID(annotation, entry.get(5));
		setCHEB(annotation, entry.get(6));
		setCAS(annotation, entry.get(7));
		setPUBC(annotation, entry.get(8));
		setPUBS(annotation, entry.get(9));
		setINCH(annotation, entry.get(10));
		setDRUG(annotation, entry.get(11));
		setHMBD(annotation, entry.get(12));
		setKEGG(annotation, entry.get(13));
		setKEGD(annotation, entry.get(14));
		setMESH(annotation, entry.get(15));
		setFDA(annotation, entry.get(16));
		setFDA_DATE(annotation, entry.get(17));
	}
	
	protected abstract void setFDA_DATE(A annotation, String string);
	protected abstract void setFDA(A annotation, String string);
	protected abstract void setMESH(A annotation, String string);
	protected abstract void setKEGD(A annotation, String string);
	protected abstract void setKEGG(A annotation, String string);
	protected abstract void setHMBD(A annotation, String string);
	protected abstract void setDRUG(A annotation, String string);
	protected abstract void setINCH(A annotation, String string);
	protected abstract void setPUBS(A annotation, String string);
	protected abstract void setPUBC(A annotation, String string);
	protected abstract void setCAS(A annotation, String string);
	protected abstract void setCHEB(A annotation, String string);
	protected abstract void setCHID(A annotation, String string);
	protected abstract void setType(A annotation, String string);

	protected abstract A createAnnotation(D data, int start, int end, String form);
}
