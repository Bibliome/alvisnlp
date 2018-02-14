
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
public class AlvisRelation_Type extends AnnotationBase_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AlvisRelation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
 
  /** @generated */
  final Feature casFeat_features;
  /** @generated */
  final int     casFeatCode_features;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getFeatures(int addr) {
        if (featOkTst && casFeat_features == null)
      jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_features);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFeatures(int addr, int v) {
        if (featOkTst && casFeat_features == null)
      jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    ll_cas.ll_setRefValue(addr, casFeatCode_features, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getFeatures(int addr, int i) {
        if (featOkTst && casFeat_features == null)
      jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_features), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_features), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_features), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setFeatures(int addr, int i, int v) {
        if (featOkTst && casFeat_features == null)
      jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_features), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_features), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_features), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_tuples;
  /** @generated */
  final int     casFeatCode_tuples;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTuples(int addr) {
        if (featOkTst && casFeat_tuples == null)
      jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_tuples);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTuples(int addr, int v) {
        if (featOkTst && casFeat_tuples == null)
      jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    ll_cas.ll_setRefValue(addr, casFeatCode_tuples, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getTuples(int addr, int i) {
        if (featOkTst && casFeat_tuples == null)
      jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tuples), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tuples), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tuples), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setTuples(int addr, int i, int v) {
        if (featOkTst && casFeat_tuples == null)
      jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tuples), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_tuples), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_tuples), i, v);
  }
 
 
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
      jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AlvisRelation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_features = jcas.getRequiredFeatureDE(casType, "features", "uima.cas.FSArray", featOkTst);
    casFeatCode_features  = (null == casFeat_features) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_features).getCode();

 
    casFeat_tuples = jcas.getRequiredFeatureDE(casType, "tuples", "uima.cas.FSArray", featOkTst);
    casFeatCode_tuples  = (null == casFeat_tuples) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tuples).getCode();

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

  }
}



    