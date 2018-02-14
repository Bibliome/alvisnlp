

/* First created by JCasGen Tue Feb 13 16:59:04 CET 2018 */
package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** Feature key-value pair of an Element
 * Updated by JCasGen Wed Feb 14 10:02:16 CET 2018
 * XML source: /home/rbossy/code/alvisnlp/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml
 * @generated */
public class AlvisFeature extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AlvisFeature.class);
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
  protected AlvisFeature() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AlvisFeature(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AlvisFeature(JCas jcas) {
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
  //* Feature: key

  /** getter for key - gets 
   * @generated
   * @return value of the feature 
   */
  public String getKey() {
    if (AlvisFeature_Type.featOkTst && ((AlvisFeature_Type)jcasType).casFeat_key == null)
      jcasType.jcas.throwFeatMissing("key", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AlvisFeature_Type)jcasType).casFeatCode_key);}
    
  /** setter for key - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setKey(String v) {
    if (AlvisFeature_Type.featOkTst && ((AlvisFeature_Type)jcasType).casFeat_key == null)
      jcasType.jcas.throwFeatMissing("key", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((AlvisFeature_Type)jcasType).casFeatCode_key, v);}    
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (AlvisFeature_Type.featOkTst && ((AlvisFeature_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisFeature");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AlvisFeature_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (AlvisFeature_Type.featOkTst && ((AlvisFeature_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.AlvisFeature");
    jcasType.ll_cas.ll_setStringValue(addr, ((AlvisFeature_Type)jcasType).casFeatCode_value, v);}    
  }

    