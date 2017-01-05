/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package org.bibliome.alvisnlp.modules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

import org.bibliome.alvisnlp.modules.SectionModule.SectionResolvedObjects;
import org.bibliome.util.Iterators;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.DownCastElement;
import alvisnlp.corpus.Element;
import alvisnlp.corpus.Layer;
import alvisnlp.corpus.Relation;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.corpus.expressions.EvaluationContext;
import alvisnlp.corpus.expressions.ResolverException;
import alvisnlp.module.ModuleException;
import alvisnlp.module.ProcessingContext;
import alvisnlp.module.lib.AlvisNLPModule;
import alvisnlp.module.lib.Param;

@AlvisNLPModule(beta=true)
public class FillDB extends SectionModule<SectionResolvedObjects> {
	private String jdbcDriver = "org.postgresql.Driver";
	private String url;
	private String username;
	private String password;
	private String schema;

	@Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
		//logger.info("bufferdb scheme feeding ...");

		Connection connection = null;

		try { // load the driver
//			Class.forName("org.postgresql.Driver");
			Class.forName("org.postgresql.Driver", true, FillDB.class.getClassLoader());
		}
		catch ( ClassNotFoundException ex ) { // problem loading driver
			ex.printStackTrace();
			return;
		}

		try {
			connection = DriverManager.getConnection(url,username,password);
			//logger.info("Connection Successful !");


			// {START Through all Documents}
			for(Document doc: Iterators.loop(documentIterator(evalCtx, corpus))) {

				String doc_id = doc.getId(); // get the document id

				
				// insertion of a Document
				Statement insertDocument = connection.createStatement();
				insertDocument.executeUpdate("INSERT INTO " + schema + ".documents (doc_id) VALUES ('"+ doc_id +"')");
				insertDocument.close();

				// {START features-document}
				Element elt = doc;
				for (String feat_name : elt.getFeatureKeys()) {
				    for (String feat_value : elt.getFeature(feat_name)) { // FIXME : récupère aussi l'attribut "id" du document ! Attention doublon !
				    	//logger.info("Document : " + doc_id + " ; Feature : " + feat_name + " ; FeatValue : " + feat_value);
				    	// checking document feature existence(s) >> adding it (them) and link it (them) to the Document
						Statement selectFeature = connection.createStatement();
						ResultSet rs = selectFeature.executeQuery("SELECT feat_id FROM " + schema + ".features WHERE feat_name = '" + feat_name + "' AND feat_value = '" + feat_value + "'");
						//logger.info("SELECT done ! Document : " + doc_id);


						int feat_id;
						if (rs.next()) { // existing feature >> get feat_id back
							feat_id = rs.getInt(1);
							selectFeature.close();
						} else { // new feature >> insertion into Features table returning feat_id
							Statement insertFeature = connection.createStatement();
							insertFeature.executeUpdate("INSERT INTO " + schema + ".features (feat_name,feat_value) VALUES ('" + feat_name + "','"+ feat_value +"')");
							insertFeature.close();
							//logger.info("INSERT done ! Document : " + doc_id);

							selectFeature.close();
							selectFeature = connection.createStatement();
							rs = selectFeature.executeQuery("SELECT feat_id FROM " + schema + ".features WHERE feat_name = '" + feat_name + "' AND feat_value = '" + feat_value + "'");
							rs.next();
							feat_id = rs.getInt(1);
							selectFeature.close();
						}

						// Link features and Documents
						Statement insertDoc_Contain_Feat = connection.createStatement();
						insertDoc_Contain_Feat.executeUpdate("INSERT INTO " + schema + ".doc_contain_feat (doc_id,feat_id) VALUES ('"+ doc_id +"',"+ feat_id +")");
						insertDoc_Contain_Feat.close();
						
				    }
				} // {END features-document}
				

				// {START Through all sections}
				for(Section sec: Iterators.loop(doc.sectionIterator())) {

					// get sec_name
					String sec_name = sec.getName();
					// get sec_text
					String sec_text = sec.getContents();
					//logger.info("Document : " + doc_id + " ; sec_name : " + sec_name + " ; sec_text : " + sec_text);

					// insertion of a Section
					PreparedStatement insertSection = connection.prepareStatement ("INSERT INTO " + schema + ".sections (doc_id,sec_name,sec_text) VALUES ( ? , ? , ? )");
					insertSection.setString(1,doc_id);
					insertSection.setString(2,sec_name);
					insertSection.setString(3,sec_text);
					insertSection.execute();
					insertSection.clearParameters();
					insertSection.close();
					//logger.info("Section INSERT done ! Document : " + doc_id);

					// retrieve sec_id
					PreparedStatement selectSection = connection.prepareStatement ("SELECT sec_id FROM " + schema + ".sections WHERE doc_id = ? AND sec_name = ? AND sec_text = ? ");
					selectSection.setString(1,doc_id);
					selectSection.setString(2,sec_name);
					selectSection.setString(3,sec_text);
					ResultSet retrieveSec_id = selectSection.executeQuery();
					retrieveSec_id.next();
					int sec_id = retrieveSec_id.getInt(1);
					selectSection.clearParameters();
					selectSection.close();
					//logger.info("SELECT done ! sec_id : " + sec_id + " Document : " + doc_id);
					
					// {START Through all section features}
					for (String feat_name : sec.getFeatureKeys()) {
					    for (String feat_value : sec.getFeature(feat_name)) { 
					    	logger.info("Section : " + sec_id + " ; Feature : " + feat_name + " ; FeatValue : " + feat_value);
					    	// checking section feature existence(s) >> adding it (them) and link it (them) to the Section
							Statement selectFeature = connection.createStatement();
							ResultSet rs = selectFeature.executeQuery("SELECT feat_id FROM " + schema + ".features WHERE feat_name = '" + feat_name + "' AND feat_value = '" + feat_value + "'");
							//logger.info("SELECT done ! Document : " + doc_id);


							int feat_id;
							if (rs.next()) { // existing feature >> get feat_id back
								feat_id = rs.getInt(1);
								selectFeature.close();
							} else { // new feature >> insertion into Features table returning feat_id
								Statement insertFeature = connection.createStatement();
								insertFeature.executeUpdate("INSERT INTO " + schema + ".features (feat_name,feat_value) VALUES ('" + feat_name + "','"+ feat_value +"')");
								insertFeature.close();
								//logger.info("INSERT done ! Document : " + doc_id);

								selectFeature.close();
								selectFeature = connection.createStatement();
								rs = selectFeature.executeQuery("SELECT feat_id FROM " + schema + ".features WHERE feat_name = '" + feat_name + "' AND feat_value = '" + feat_value + "'");
								rs.next();
								feat_id = rs.getInt(1);
								selectFeature.close();
							}

							// Link features and Documents
							Statement insertSec_Contain_Feat = connection.createStatement();
							insertSec_Contain_Feat.executeUpdate("INSERT INTO " + schema + ".sec_contain_feat (sec_id,feat_id) VALUES ('"+ sec_id +"',"+ feat_id +")");
							insertSec_Contain_Feat.close();
							
					    }
					} // {END Through all section features}



					// {START Through all annotations}
					for(Annotation annot: sec.getAllAnnotations()) {

						// get id, start and end
						int annot_id = annot.hashCode();
						int annot_start = annot.getStart();
						int annot_end = annot.getEnd();

						// insertion of an annotation if it does not exist and link it to the section !
						Statement selectAnnot = connection.createStatement();
						ResultSet retrieveAnnot_id = selectAnnot.executeQuery("SELECT annot_id FROM " + schema + ".annotations WHERE annot_id = " + annot_id + "");
						//logger.info("Annotation Control SELECT done ! Document : " + doc_id);
						if (retrieveAnnot_id.next()) {

						} else {
							Statement insertAnnot = connection.createStatement();
							insertAnnot.executeUpdate("INSERT INTO " + schema + ".annotations (annot_id,annot_start,annot_end) VALUES ("+ annot_id +","+ annot_start +","+ annot_end +")");
							insertAnnot.close();
							//logger.info("Annotation INSERT done ! Document : " + doc_id);

							Statement insertSec_Contain_Annot = connection.createStatement();
							insertSec_Contain_Annot.executeUpdate("INSERT INTO " + schema + ".sec_contain_annot (sec_id,annot_id) VALUES ("+ sec_id +","+ annot_id +")");
							insertSec_Contain_Annot.close();
							//logger.info("Section linked to Annotation INSERT done ! Document : " + doc_id);
						}
						selectAnnot.close();



						// {START Through all annotation features}
						for (String feat_name : annot.getFeatureKeys()) {

							// get the list of feat_value for that feat_name
							List<String> listOf_feat_value = annot.getFeature(feat_name);

							// FIXME : may be useful to add the feature "form" and its value "getForm()" ???
							// checking annotation feature existence(s) >> adding it (them) and link it (them) to the Annotation
							for(String feat_value : listOf_feat_value) {
								PreparedStatement selectFeatureAnnot = connection.prepareStatement ("SELECT feat_id FROM " + schema + ".features WHERE feat_name = ? AND feat_value = ? ");
								selectFeatureAnnot.setString(1,feat_name);
								selectFeatureAnnot.setString(2,feat_value);
								ResultSet retrieveFeat_id = selectFeatureAnnot.executeQuery();
								//logger.info("Feature Annotation First SELECT done ! Document : " + doc_id);


								int feat_id;
								if (retrieveFeat_id.next()) { // existing feature >> get feat_id back
									feat_id = retrieveFeat_id.getInt(1);
									selectFeatureAnnot.clearParameters();
									selectFeatureAnnot.close();
									//logger.info("Existing Feature Id retrieved ! Document : " + doc_id);
								} else { // new feature >> insertion into Features relation returning feat_id
									PreparedStatement insertFeatureAnnotation = connection.prepareStatement ("INSERT INTO " + schema + ".features (feat_name,feat_value) VALUES ( ? , ? )");
									insertFeatureAnnotation.setString(1,feat_name);
									insertFeatureAnnotation.setString(2,feat_value);
									insertFeatureAnnotation.execute();
									insertFeatureAnnotation.clearParameters();
									insertFeatureAnnotation.close();
									//logger.info("Feature Annotation INSERT done ! Document : " + doc_id);

									selectFeatureAnnot.clearParameters();
									selectFeatureAnnot.close();
									selectFeatureAnnot = connection.prepareStatement ("SELECT feat_id FROM " + schema + ".features WHERE feat_name = ? AND feat_value = ? ");
									selectFeatureAnnot.setString(1,feat_name);
									selectFeatureAnnot.setString(2,feat_value);
									retrieveFeat_id = selectFeatureAnnot.executeQuery();
									retrieveFeat_id.next();
									feat_id = retrieveFeat_id.getInt(1);
									selectFeatureAnnot.close();
								}

								// Link feature and Annotation if does not exist

								Statement selectAnnot_Contain_Feat = connection.createStatement();
								ResultSet retrieveLink = selectAnnot_Contain_Feat.executeQuery("SELECT * FROM " + schema + ".annot_contain_feat WHERE annot_id = " + annot_id + " AND feat_id = " + feat_id + "");
								//logger.info("Annot_Contain_Feat SELECT done ! Document : " + doc_id);

								if (retrieveLink.next()) { // existing link between annotation and feature >> go next !

								} else { // ready to link annotation and feature !
									//logger.info("Ready to link Annotation and Feature : " + annot_id + " / " + feat_id);
									Statement insertAnnot_Contain_Feat = connection.createStatement();
									insertAnnot_Contain_Feat.executeUpdate("INSERT INTO " + schema + ".annot_contain_feat (annot_id,feat_id) VALUES ("+ annot_id +","+ feat_id +")");
									insertAnnot_Contain_Feat.close();
									//logger.info("Feature and Annotation linked ! Document : " + doc_id);
								}
								selectAnnot_Contain_Feat.close();

							}

						}// {END Through all annotation features}

					} // {END Through all annotations}

					// {START Through all layers}
					for(Layer lay : sec.getAllLayers()){

						// get lay_name and insert it if it doesn't exist and/or retrieve it's id
						String lay_name = lay.getName().toString();

						//logger.info("Looking for layer :" + lay_name + " Document : " + doc_id);
						Statement selectLayer = connection.createStatement();
						ResultSet retrieveLay_id = selectLayer.executeQuery("SELECT lay_id FROM " + schema + ".layers WHERE lay_name = '" + lay_name + "'");
						//logger.info("layers First SELECT done ! Document : " + doc_id);

						int lay_id;
						if (retrieveLay_id.next()) { // existing layer >> retrieves it's id
							lay_id = retrieveLay_id.getInt(1);
							selectLayer.close();
							//logger.info("lay_id of known layer retrieved ! Document : " + doc_id);
						} else { // new layer ! Insertion and retrieving of it's id !
							//logger.info("Ready to insert the new Layer ! Document : " + doc_id);
							Statement insertLayer = connection.createStatement();
							insertLayer.executeUpdate("INSERT INTO " + schema + ".layers (lay_name) VALUES ('"+ lay_name +"')");
							insertLayer.close();
							//logger.info("Layer insertion done ! Document : " + doc_id);

							selectLayer.close();
							selectLayer = connection.createStatement();
							retrieveLay_id = selectLayer.executeQuery("SELECT lay_id FROM " + schema + ".layers WHERE lay_name = '" + lay_name + "'");
							retrieveLay_id.next();
							lay_id = retrieveLay_id.getInt(1);
							selectLayer.close();
							//logger.info("lay_id of new layer retrieved ! Document : " + doc_id);
						}

						// Link Layer and Annotations if do not exist in lay_contain_annot table
						for (Annotation annot: Iterators.loop(lay.iterator())) {
							int annot_id = annot.hashCode();

							Statement selectLay_Contain_Annot = connection.createStatement();
							ResultSet retrieveLinkLayAnnot = selectLay_Contain_Annot.executeQuery("SELECT * FROM " + schema + ".lay_contain_annot WHERE lay_id = " + lay_id + " AND annot_id = " + annot_id + "");
							//logger.info("Lay_Contain_Annot SELECT done ! Document : " + doc_id);

							if (retrieveLinkLayAnnot.next()) { // existing link between annotation and layer >> go next !

							} else { // ready to link annotation and layer !
								//logger.info("Ready to link Annotation and Layer : " + annot_id + " / " + lay_id + " Document : " + doc_id);
								Statement insertLay_Contain_Annot = connection.createStatement();
								insertLay_Contain_Annot.executeUpdate("INSERT INTO " + schema + ".lay_contain_annot (lay_id,annot_id) VALUES ("+ lay_id +","+ annot_id +")");
								insertLay_Contain_Annot.close();
								//logger.info("Layer and Annotation linked ! Document : " + doc_id);
							}
							selectLay_Contain_Annot.close();
						}

					} // {END Through all layers}

					// {START Through all relations}
					for(Relation rel : sec.getAllRelations()){

						// get relation name. Corresponds to attribute "name" of the tag <relation></relation>
						String rel_name = rel.getName();

						// {START Through all Tuples}
						// Each tuple generate an entry (and a rel_id) into table Relations
						for(Tuple tup: rel.getTuples()){

							// Create an entry into Relations and retrieve the rel_id which identifies the tuple
//							logger.info("Ready to create relation (= tuple) entry from Relation : " + rel_name + " ; of Document " + doc_id);
							Statement insertRelation = connection.createStatement();
							insertRelation.executeUpdate("INSERT INTO " + schema + ".relations (rel_name) VALUES ('"+ rel_name +"')");
							insertRelation.close();

							Statement selectRelation = connection.createStatement();
							ResultSet retrieveRel_id = selectRelation.executeQuery("SELECT rel_id FROM " + schema + ".relations WHERE rel_name = '" + rel_name + "' ORDER BY rel_id DESC LIMIT 1");
							retrieveRel_id.next();
							int rel_id = retrieveRel_id.getInt(1);
							selectRelation.close();
//							logger.info("Relation (= Tuple) created ! rel_id is : " + rel_id + " ; for Document : " + doc_id);

							// {START Through all relation (=tuple) features}
							for (String feat_name : tup.getFeatureKeys()) {

								// get the list of feat_value for that feat_name
								List<String> listOf_feat_value = tup.getFeature(feat_name);


								// checking relation (=tuple) feature existence(s) >> adding it (them) and link it (them) to the Relation (=tuple)
								for(String feat_value : listOf_feat_value) {
									PreparedStatement selectFeatureRel = connection.prepareStatement ("SELECT feat_id FROM " + schema + ".features WHERE feat_name = ? AND feat_value = ? ");
									selectFeatureRel.setString(1,feat_name);
									selectFeatureRel.setString(2,feat_value);
									ResultSet retrieveFeat_id = selectFeatureRel.executeQuery();
									//logger.info("Feature Relation (=tuple) First SELECT done ! Document : " + doc_id);


									int feat_id;
									if (retrieveFeat_id.next()) { // existing feature >> get feat_id back
										feat_id = retrieveFeat_id.getInt(1);
										selectFeatureRel.clearParameters();
										selectFeatureRel.close();
										//logger.info("Existing Feature Id retrieved ! Document : " + doc_id);
									} else { // new feature >> insertion into Features table returning feat_id
										PreparedStatement insertFeatureRel = connection.prepareStatement ("INSERT INTO " + schema + ".features (feat_name,feat_value) VALUES ( ? , ? )");
										insertFeatureRel.setString(1,feat_name);
										insertFeatureRel.setString(2,feat_value);
										insertFeatureRel.execute();
										insertFeatureRel.clearParameters();
										insertFeatureRel.close();
										//logger.info("Feature Annotation INSERT done ! Document : " + doc_id);

										selectFeatureRel.clearParameters();
										selectFeatureRel.close();
										selectFeatureRel = connection.prepareStatement ("SELECT feat_id FROM " + schema + ".features WHERE feat_name = ? AND feat_value = ? ");
										selectFeatureRel.setString(1,feat_name);
										selectFeatureRel.setString(2,feat_value);
										retrieveFeat_id = selectFeatureRel.executeQuery();
										retrieveFeat_id.next();
										feat_id = retrieveFeat_id.getInt(1);
										selectFeatureRel.close();
									}

									// Link feature and Relation (=tuple) if does not exist

									Statement selectRel_Contain_Feat = connection.createStatement();
									ResultSet retrieveLinkRelFeat = selectRel_Contain_Feat.executeQuery("SELECT * FROM " + schema + ".rel_contain_feat WHERE rel_id = " + rel_id + " AND feat_id = " + feat_id + "");
//									logger.info("Rel_Contain_Feat SELECT done ! Document : " + doc_id);

									if (retrieveLinkRelFeat.next()) { // existing link between relation (=tuple) and feature >> go next !

									} else { // ready to link relation (=tuple) and feature !
//										logger.info("Ready to link Relation (=tuple) and Feature : " + rel_id + " / " + feat_id);
										Statement insertRel_Contain_Feat = connection.createStatement();
										insertRel_Contain_Feat.executeUpdate("INSERT INTO " + schema + ".rel_contain_feat (rel_id,feat_id) VALUES ("+ rel_id +","+ feat_id +")");
										insertRel_Contain_Feat.close();
//										logger.info("Feature and Relation (=tuple) linked ! Document : " + doc_id);
									}
									selectRel_Contain_Feat.close();

								}

							}// {END Through all relation (=tuple) features}

							// {START Through all relation (=tuple) roles}
							// each role/argument is an entry into table rel_link_annot
							for (String annot_role : tup.getRoles()) {

								// get the annotation id using the role !
								Annotation annot = DownCastElement.toAnnotation(tup.getArgument(annot_role));
								int annot_id = annot.hashCode();
//								logger.info("For Role : " + annot_role + " ; of Relation : " + rel_name + " ; of Document : " + doc_id + " ; Annotation is " + annot_id);


								// Link Annotation and Relation (=tuple) if does not exist

								Statement selectRel_Link_Annot = connection.createStatement();
								ResultSet retrieveLinkRelAnnot = selectRel_Link_Annot.executeQuery("SELECT * FROM " + schema + ".rel_link_annot WHERE rel_id = " + rel_id + " AND annot_id = " + annot_id + " AND annot_role = '" + annot_role + "'");
//								logger.info("Rel_Link_Annot SELECT done ! Document : " + doc_id);

								if (retrieveLinkRelAnnot.next()) { // existing link between relation (=tuple) and Annotation for the role >> go next !

								} else { // ready to link relation (=tuple) and Annotation for the Role !
//									logger.info("Ready to link Relation (=tuple) and Annotation for the Role : " + rel_id + " / " + annot_id + " / " + annot_role);
									Statement insertRel_Link_Annot = connection.createStatement();
									insertRel_Link_Annot.executeUpdate("INSERT INTO " + schema + ".rel_link_annot (rel_id,annot_id,annot_role) VALUES ("+ rel_id +","+ annot_id +",'" + annot_role + "')");
									insertRel_Link_Annot.close();
//									logger.info("Annotation and Relation (=tuple) linked for the Role ! Document : " + doc_id);
								}
								selectRel_Link_Annot.close();



							} // {END Through all relation (=tuple) roles}

						} // {END Through all Tuples}

					} // {END Through all relations}

				} // {END Through all sections}

			} // {END Through all Documents}



		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		finally {
			if( connection != null ) {
				try {
					connection.close();
				}
				catch ( SQLException ex ) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	@Override
	protected String[] addLayersToSectionFilter() {
		return null;
	}

	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param
	public String getJdbcDriver() {
		return jdbcDriver;
	}

	@Param
	public String getUrl() {
		return url;
	}

	@Param
	public String getUsername() {
		return username;
	}

	@Param
	public String getPassword() {
		return password;
	}

	@Param
	public String getSchema() {
		return schema;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
}
