
/* First created by JCasGen Tue Feb 13 16:59:04 CET 2018 */
package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.AnnotationBase_Type;

/** 
 * Updated by JCasGen Wed Feb 14 10:02:16 CET 2018
 * @generated */
public class AlvisLayer_Type extends AnnotationBase_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AlvisLayer.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  
 
  /** @generated */
  final Feature casFeat_annotations;
  /** @generated */
  final int     casFeatCode_annotations;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAnnotations(int addr) {
        if (featOkTst && casFeat_annotations == null)
      jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
    return ll_cas.ll_getRefValue(addr, casFeatCode_annotations);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnnotations(int addr, int v) {
        if (featOkTst && casFeat_annotations == null)
      jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
    ll_cas.ll_setRefValue(addr, casFeatCode_annotations, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getAnnotations(int addr, int i) {
        if (featOkTst && casFeat_annotations == null)
      jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_annotations), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_annotations), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_annotations), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setAnnotations(int addr, int i, int v) {
        if (featOkTst && casFeat_annotations == null)
      jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisLayer");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_annotations), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_annotations), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_annotations), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AlvisLayer_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

 
    casFeat_annotations = jcas.getRequiredFeatureDE(casType, "annotations", "uima.cas.FSArray", featOkTst);
    casFeatCode_annotations  = (null == casFeat_annotations) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_annotations).getCode();

  }
}



    