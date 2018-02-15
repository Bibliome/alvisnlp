

/* First created by JCasGen Thu Feb 15 18:58:28 CET 2018 */
package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.AnnotationBase;


/** 
 * Updated by JCasGen Thu Feb 15 18:58:28 CET 2018
 * XML source: /home/rbossy/code/alvisnlp/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml
 * @generated */
public class DocumentProxy extends AnnotationBase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentProxy.class);
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
  protected DocumentProxy() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocumentProxy(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocumentProxy(JCas jcas) {
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
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: features

  /** getter for features - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getFeatures() {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_features)));}
    
  /** setter for features - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeatures(FSArray v) {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_features, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for features - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public FeatureProxy getFeatures(int i) {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_features), i);
    return (FeatureProxy)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_features), i)));}

  /** indexed setter for features - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setFeatures(int i, FeatureProxy v) { 
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_features), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_features), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: sections

  /** getter for sections - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getSections() {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_sections == null)
      jcasType.jcas.throwFeatMissing("sections", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_sections)));}
    
  /** setter for sections - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSections(FSArray v) {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_sections == null)
      jcasType.jcas.throwFeatMissing("sections", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_sections, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for sections - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public SectionProxy getSections(int i) {
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_sections == null)
      jcasType.jcas.throwFeatMissing("sections", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_sections), i);
    return (SectionProxy)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_sections), i)));}

  /** indexed setter for sections - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSections(int i, SectionProxy v) { 
    if (DocumentProxy_Type.featOkTst && ((DocumentProxy_Type)jcasType).casFeat_sections == null)
      jcasType.jcas.throwFeatMissing("sections", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.DocumentProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_sections), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentProxy_Type)jcasType).casFeatCode_sections), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    