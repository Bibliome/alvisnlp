

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
public class LayerProxy extends AnnotationBase {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(LayerProxy.class);
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
  protected LayerProxy() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public LayerProxy(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public LayerProxy(JCas jcas) {
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
  //* Feature: name

  /** getter for name - gets 
   * @generated
   * @return value of the feature 
   */
  public String getName() {
    if (LayerProxy_Type.featOkTst && ((LayerProxy_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy");
    return jcasType.ll_cas.ll_getStringValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setName(String v) {
    if (LayerProxy_Type.featOkTst && ((LayerProxy_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy");
    jcasType.ll_cas.ll_setStringValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_name, v);}    
   
    
  //*--------------*
  //* Feature: annotations

  /** getter for annotations - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getAnnotations() {
    if (LayerProxy_Type.featOkTst && ((LayerProxy_Type)jcasType).casFeat_annotations == null)
      jcasType.jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_annotations)));}
    
  /** setter for annotations - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotations(FSArray v) {
    if (LayerProxy_Type.featOkTst && ((LayerProxy_Type)jcasType).casFeat_annotations == null)
      jcasType.jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy");
    jcasType.ll_cas.ll_setRefValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_annotations, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for annotations - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public AnnotationProxy getAnnotations(int i) {
    if (LayerProxy_Type.featOkTst && ((LayerProxy_Type)jcasType).casFeat_annotations == null)
      jcasType.jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_annotations), i);
    return (AnnotationProxy)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_annotations), i)));}

  /** indexed setter for annotations - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setAnnotations(int i, AnnotationProxy v) { 
    if (LayerProxy_Type.featOkTst && ((LayerProxy_Type)jcasType).casFeat_annotations == null)
      jcasType.jcas.throwFeatMissing("annotations", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.LayerProxy");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_annotations), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((LayerProxy_Type)jcasType).casFeatCode_annotations), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    