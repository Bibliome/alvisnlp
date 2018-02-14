

/* First created by JCasGen Tue Feb 13 16:59:04 CET 2018 */
package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.AnnotationBase;


/** 
 * Updated by JCasGen Wed Feb 14 10:02:16 CET 2018
 * XML source: /home/rbossy/code/alvisnlp/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml
 * @generated */
public class AlvisRelation extends AnnotationBase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AlvisRelation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected AlvisRelation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AlvisRelation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AlvisRelation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: features

  /** getter for features - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getFeatures() {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_features)));}
    
  /** setter for features - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeatures(FSArray v) {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_features, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for features - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public AlvisFeature getFeatures(int i) {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_features), i);
    return (AlvisFeature)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_features), i)));}

  /** indexed setter for features - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setFeatures(int i, AlvisFeature v) { 
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_features), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_features), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: tuples

  /** getter for tuples - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getTuples() {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_tuples == null)
      jcasType.jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_tuples)));}
    
  /** setter for tuples - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTuples(FSArray v) {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_tuples == null)
      jcasType.jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_tuples, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for tuples - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public AlvisTuple getTuples(int i) {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_tuples == null)
      jcasType.jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_tuples), i);
    return (AlvisTuple)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_tuples), i)));}

  /** indexed setter for tuples - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTuples(int i, AlvisTuple v) { 
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_tuples == null)
      jcasType.jcas.throwFeatMissing("tuples", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_tuples), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_tuples), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: name

  /** getter for name - gets 
   * @generated
   * @return value of the feature 
   */
  public String getName() {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    if (AlvisRelation_Type.featOkTst && ((AlvisRelation_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisRelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((AlvisRelation_Type)jcasType).casFeatCode_name, v);}    
  }

    