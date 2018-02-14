

/* First created by JCasGen Tue Feb 13 16:59:04 CET 2018 */
package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** 
 * Updated by JCasGen Wed Feb 14 10:02:16 CET 2018
 * XML source: /home/rbossy/code/alvisnlp/alvisnlp-bibliome/src/main/resources/fr/inra/maiage/bibliome/alvisnlp/bibliomefactory/modules/uima/uima-document.xml
 * @generated */
public class TupleArgument extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(TupleArgument.class);
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
  protected TupleArgument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TupleArgument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TupleArgument(JCas jcas) {
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
  //* Feature: role

  /** getter for role - gets 
   * @generated
   * @return value of the feature 
   */
  public String getRole() {
    if (TupleArgument_Type.featOkTst && ((TupleArgument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleArgument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TupleArgument_Type)jcasType).casFeatCode_role);}
    
  /** setter for role - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRole(String v) {
    if (TupleArgument_Type.featOkTst && ((TupleArgument_Type)jcasType).casFeat_role == null)
      jcasType.jcas.throwFeatMissing("role", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleArgument");
    jcasType.ll_cas.ll_setStringValue(addr, ((TupleArgument_Type)jcasType).casFeatCode_role, v);}    
   
    
  //*--------------*
  //* Feature: argument

  /** getter for argument - gets 
   * @generated
   * @return value of the feature 
   */
  public TOP getArgument() {
    if (TupleArgument_Type.featOkTst && ((TupleArgument_Type)jcasType).casFeat_argument == null)
      jcasType.jcas.throwFeatMissing("argument", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleArgument");
    return (TOP)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((TupleArgument_Type)jcasType).casFeatCode_argument)));}
    
  /** setter for argument - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setArgument(TOP v) {
    if (TupleArgument_Type.featOkTst && ((TupleArgument_Type)jcasType).casFeat_argument == null)
      jcasType.jcas.throwFeatMissing("argument", "fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.uima.types.TupleArgument");
    jcasType.ll_cas.ll_setRefValue(addr, ((TupleArgument_Type)jcasType).casFeatCode_argument, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    