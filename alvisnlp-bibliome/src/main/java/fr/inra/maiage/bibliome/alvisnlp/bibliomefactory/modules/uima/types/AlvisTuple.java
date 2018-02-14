

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
public class AlvisTuple extends AnnotationBase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AlvisTuple.class);
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
  protected AlvisTuple() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AlvisTuple(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AlvisTuple(JCas jcas) {
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
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_features)));}
    
  /** setter for features - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeatures(FSArray v) {
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_features, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for features - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public AlvisFeature getFeatures(int i) {
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_features), i);
    return (AlvisFeature)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_features), i)));}

  /** indexed setter for features - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setFeatures(int i, AlvisFeature v) { 
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_features), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_features), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: arguments

  /** getter for arguments - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getArguments() {
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_arguments)));}
    
  /** setter for arguments - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setArguments(FSArray v) {
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    jcasType.ll_cas.ll_setRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_arguments, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for arguments - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public TupleArgument getArguments(int i) {
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_arguments), i);
    return (TupleArgument)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_arguments), i)));}

  /** indexed setter for arguments - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setArguments(int i, TupleArgument v) { 
    if (AlvisTuple_Type.featOkTst && ((AlvisTuple_Type)jcasType).casFeat_arguments == null)
      jcasType.jcas.throwFeatMissing("arguments", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisTuple");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_arguments), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((AlvisTuple_Type)jcasType).casFeatCode_arguments), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    