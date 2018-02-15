

/* First created by JCasGen Thu Feb 15 18:58:28 CET 2018 */
package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Feb 15 18:58:28 CET 2018
 * XML source: /home/rbossy/code/alvisnlp/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml
 * @generated */
public class SectionProxy extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SectionProxy.class);
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
  protected SectionProxy() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SectionProxy(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SectionProxy(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SectionProxy(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  //* Feature: name

  /** getter for name - gets 
   * @generated
   * @return value of the feature 
   */
  public String getName() {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.ll_cas.ll_setStringValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: features

  /** getter for features - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getFeatures() {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_features)));}
    
  /** setter for features - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFeatures(FSArray v) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.ll_cas.ll_setRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_features, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for features - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public FeatureProxy getFeatures(int i) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_features), i);
    return (FeatureProxy)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_features), i)));}

  /** indexed setter for features - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setFeatures(int i, FeatureProxy v) { 
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_features == null)
      jcasType.jcas.throwFeatMissing("features", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_features), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_features), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: layers

  /** getter for layers - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getLayers() {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_layers == null)
      jcasType.jcas.throwFeatMissing("layers", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_layers)));}
    
  /** setter for layers - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLayers(FSArray v) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_layers == null)
      jcasType.jcas.throwFeatMissing("layers", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.ll_cas.ll_setRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_layers, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for layers - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public LayerProxy getLayers(int i) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_layers == null)
      jcasType.jcas.throwFeatMissing("layers", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_layers), i);
    return (LayerProxy)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_layers), i)));}

  /** indexed setter for layers - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setLayers(int i, LayerProxy v) { 
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_layers == null)
      jcasType.jcas.throwFeatMissing("layers", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_layers), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_layers), i, jcasType.ll_cas.ll_getFSRef(v));}
   
    
  //*--------------*
  //* Feature: relations

  /** getter for relations - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getRelations() {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_relations == null)
      jcasType.jcas.throwFeatMissing("relations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_relations)));}
    
  /** setter for relations - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRelations(FSArray v) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_relations == null)
      jcasType.jcas.throwFeatMissing("relations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.ll_cas.ll_setRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_relations, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for relations - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public RelationProxy getRelations(int i) {
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_relations == null)
      jcasType.jcas.throwFeatMissing("relations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_relations), i);
    return (RelationProxy)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_relations), i)));}

  /** indexed setter for relations - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setRelations(int i, RelationProxy v) { 
    if (SectionProxy_Type.featOkTst && ((SectionProxy_Type)jcasType).casFeat_relations == null)
      jcasType.jcas.throwFeatMissing("relations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.SectionProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_relations), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((SectionProxy_Type)jcasType).casFeatCode_relations), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    