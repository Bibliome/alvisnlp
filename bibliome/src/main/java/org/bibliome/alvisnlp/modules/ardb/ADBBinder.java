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


package org.bibliome.alvisnlp.modules.ardb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvisnlp.corpus.Annotation;
import alvisnlp.corpus.Corpus;
import alvisnlp.corpus.Document;
import alvisnlp.corpus.Section;
import alvisnlp.corpus.Tuple;
import alvisnlp.module.ProcessingException;

/**
 *
 * @author fpapazian
 */
public class ADBBinder {

    public static String DEFAULT_ANNOTATIONTYPE = "DefaultAnnotationType";
    public static final String ALVISNLP_NS = "AlvisNLP";
    public static final String IMPORTED_ANNSET_NAME = "imported-annset";
    public static final String DocumentMeta_AnnType = "meta-document";
    public static final String AspectMeta_AnnType = "meta-aspect";
    public static final String Document_TextScope = "ts:document";
    public static final String Aspect_TextScope = "ts:aspect";
    public static final String Fragment_TextScope = "ts:frag";

    public static enum AnnotationKind {

        TextBound,
        Relation,
        Group,
    }
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";

    //-- -------------------------------------------------------------------- --
    private static Connection getConnection(ADBBinder params) throws ProcessingException {
        try {
//    		Class.forName("org.postgresql.Driver");
    		Class.forName("org.postgresql.Driver", true, ADBBinder.class.getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new ProcessingException("Missing PostgreSQL driver!", ex);
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(params.getUrl(), params.getUsername(), params.getPassword());
        } catch (SQLException ex) {
            throw new ProcessingException("Could not create connection to specified database!", ex);
        }
        if (params.getSchema() != null && !params.getSchema().isEmpty()) {
            PreparedStatement statement = null;
            try {
                //Note: PostgreSQL driver (v9.1) will throw a SQLFeatureNotSupportedException when using connection.setSchema()
                try {
                    statement = connection.prepareStatement("SET search_path TO \"" + params.getSchema() + "\" ");
                    statement.executeUpdate();

                } catch (SQLException ex) {
                    throw new ProcessingException("Could not switch to specified schema!", ex);
                }
            } finally {
                closeIfOpen(statement);
            }
        }
        return connection;
    }

    static void closeIfOpen(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            Logger.getLogger(ADBBinder.class.getName()).log(Level.WARNING, "Could not close ResultSet!", e);
        }
    }

    static void closeIfOpen(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            Logger.getLogger(ADBBinder.class.getName()).log(Level.WARNING, "Could not close Statement!", e);
        }
    }

    private static void closeIfOpen(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.getLogger(ADBBinder.class.getName()).log(Level.WARNING, "Could not close Connection!", e);
        }
    }

    public static int executeStatementAndGetId(PreparedStatement statement) throws ProcessingException {
        try {
            int inserted = statement.executeUpdate();
            if (inserted == 0) {
                throw new ProcessingException("Unexpected state : no row created by insert statement!");
            }
            ResultSet generatedId = statement.getGeneratedKeys();
            try {
                if (generatedId.next()) {
                    return generatedId.getInt(1);
                } else {
                    throw new ProcessingException("Unexpected state : Id generated by insert could not be retrieved!");
                }
            } finally {
                closeIfOpen(generatedId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ADBBinder.class.getName()).log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not execute statement!", ex);
        }

    }

    private static enum PreparedStatementId {

        AddAnnotation,
        AddAnnotationReference,
        AddAspect,
        AddCorpus,
        AddDocument,
        AddDocumentCorpus,
        AddProperty,
        AddTextScope,
    }
    //-- -------------------------------------------------------------------- --
    private String url = null;
    private String schema = null;
    private String username = null;
    private String password = null;
    private Connection connection = null;
    private Logger logger;
    private Map<PreparedStatementId, PreparedStatement> preparedStatement = new HashMap<>();

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.trim();
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = (schema != null && !schema.trim().isEmpty()) ? schema.trim() : null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void initConnection() throws ProcessingException {
        this.connection = ADBBinder.getConnection(this);
    }

    public void finishConnection() {
        for (PreparedStatement statement : preparedStatement.values()) {
            closeIfOpen(statement);
        }
        preparedStatement.clear();
        closeIfOpen(connection);
        connection = null;
    }

    public void addAnnReferencesToNakedSecondaryAnnotation(int dbAnnId, Tuple secAnnReferencesHolder, Map<String, Integer> dbAnIdByAlvisAnnId) throws ProcessingException {
        PreparedStatement addAnnRefStatement = getPreparedStatement(PreparedStatementId.AddAnnotationReference);
        try {
            int ordNum = 0;
            for (String role : secAnnReferencesHolder.getRoles()) {
                String referencedAnnAlvisId = secAnnReferencesHolder.getArgument(role).getStringId();
                int referencedAnnId = dbAnIdByAlvisAnnId.get(referencedAnnAlvisId);
                addAnnRefStatement.setInt(1, dbAnnId);
                addAnnRefStatement.setInt(2, referencedAnnId);
                addAnnRefStatement.setString(3, role);
                addAnnRefStatement.setInt(4, ordNum++);
                addAnnRefStatement.addBatch();
            }
            addAnnRefStatement.executeBatch();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not add Annotation references", ex);
        }
    }

    public int addNakedSecondaryAnnotation(AnnotationKind kind, String annotationType, Map<String, List<String>> features) throws ProcessingException {
        PreparedStatement addAnnStatement = getPreparedStatement(PreparedStatementId.AddAnnotation);
        setAddAnnotationStatementValues(addAnnStatement, kind, IMPORTED_ANNSET_NAME, ALVISNLP_NS, annotationType);
        int ann_id = executeStatementAndGetId(addAnnStatement);
        addAnnotationProps(ann_id, features, null);
        return ann_id;
    }

    public int addTextBoundAnnotation(int doc_id, String asp_id, List<Annotation> fragments, String annotationType) throws ProcessingException {
        PreparedStatement addAnnStatement = getPreparedStatement(PreparedStatementId.AddAnnotation);
        setAddAnnotationStatementValues(addAnnStatement, AnnotationKind.TextBound, IMPORTED_ANNSET_NAME, ALVISNLP_NS, annotationType);
        int ann_id = executeStatementAndGetId(addAnnStatement);
        if (!fragments.isEmpty()) {
            Annotation frag0 = fragments.get(0);
            addAnnotationProps(ann_id, frag0.getFeatures(), null);

            int fragNum = 0;
            for (Annotation fragment : fragments) {
                @SuppressWarnings("unused") // XXX
                int tscope_id = addTextScope(ann_id, doc_id, asp_id, fragment.getStart(), fragment.getEnd(), fragNum++, Fragment_TextScope);
            }
        }
        return ann_id;
    }

    public int addDocument(Document doc) throws ProcessingException {
        try {
            PreparedStatement addDocStatement = getPreparedStatement(PreparedStatementId.AddDocument);
            int doc_id = executeStatementAndGetId(addDocStatement);

            PreparedStatement addAnnStatement = getPreparedStatement(PreparedStatementId.AddAnnotation);
            setAddAnnotationStatementValues(addAnnStatement, AnnotationKind.TextBound, IMPORTED_ANNSET_NAME, ALVISNLP_NS, DocumentMeta_AnnType);
            int ann_id = executeStatementAndGetId(addAnnStatement);
            @SuppressWarnings("unused") // XXX
            int tscope_id = addTextScope(ann_id, doc_id, null, null, null, 0, Document_TextScope);


            PreparedStatement addPropertyStatement = getPreparedStatement(PreparedStatementId.AddProperty);
            setAddPropStatementValues(addPropertyStatement, ann_id, "DOC_ID", 1, doc.getId(), ALVISNLP_NS);
            addPropertyStatement.executeUpdate();

            addAnnotationProps(ann_id, doc.getFeatures(), null);

            return doc_id;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not add Document", ex);
        }
    }

    public void linkDocumentToCorpus(int corpus_id, int doc_id) throws ProcessingException {
        try {
            PreparedStatement statement = getPreparedStatement(PreparedStatementId.AddDocumentCorpus);
            statement.setInt(1, corpus_id);
            statement.setInt(2, doc_id);
            int inserted = statement.executeUpdate();
            if (inserted == 0) {
                throw new ProcessingException("Unexpected state : no row created by insert statement!");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not link Document to Corpus", ex);
        }
    }

    public void addAspect(int doc_id, String asp_id, Section section) throws ProcessingException {
        try {
            PreparedStatement addAspectStatement = getPreparedStatement(PreparedStatementId.AddAspect);
            addAspectStatement.setInt(1, doc_id);
            addAspectStatement.setString(2, asp_id);
            addAspectStatement.setString(3, "SECTION");
            String contents = section.getContents();
            addAspectStatement.setString(4, contents);
            addAspectStatement.setInt(5, contents.length());
            addAspectStatement.executeUpdate();

            PreparedStatement addAnnStatement = getPreparedStatement(PreparedStatementId.AddAnnotation);
            setAddAnnotationStatementValues(addAnnStatement, AnnotationKind.TextBound, IMPORTED_ANNSET_NAME, ALVISNLP_NS, AspectMeta_AnnType);
            int ann_id = executeStatementAndGetId(addAnnStatement);

            @SuppressWarnings("unused") // XXX
            int tscope_id = addTextScope(ann_id, doc_id, asp_id, null, null, 0, Aspect_TextScope);
            addAnnotationProps(ann_id, section.getFeatures(), null);

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not add Aspect!", ex);
        }
    }

    public int addDocumentScopeAnnotation(int doc_id, String annType, Map<String, List<String>> features) throws ProcessingException {
        PreparedStatement addAnnStatement = getPreparedStatement(PreparedStatementId.AddAnnotation);
        setAddAnnotationStatementValues(addAnnStatement, AnnotationKind.TextBound, IMPORTED_ANNSET_NAME, ALVISNLP_NS, annType);
        int ann_id = executeStatementAndGetId(addAnnStatement);
        @SuppressWarnings("unused") // XXX
        int tscope_id = addTextScope(ann_id, doc_id, null, null, null, 0, Document_TextScope);
        addAnnotationProps(ann_id, features, null);
        return ann_id;
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
    @SuppressWarnings("unused") //XXX
    private String getPrefixedBySchema(String name) {
        if (schema == null) {
            return name;
        } else {
            return "\"" + schema + "\"." + name;
        }
    }

    private PreparedStatement getPreparedStatement(PreparedStatementId statementId) throws ProcessingException {
        if (preparedStatement.containsKey(statementId)) {
            return preparedStatement.get(statementId);
        }

        PreparedStatement statement = null;
        try {
            switch (statementId) {
                case AddAnnotation:
                    statement = connection.prepareStatement("INSERT INTO annotations (ann_kind, setname, namespace, ann_type) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    break;
                case AddAnnotationReference:
                    statement = connection.prepareStatement("INSERT INTO annotationreferences (ann_id, ann_refid, ref_type, ordnum) VALUES (?, ?, ?, ?)");
                    break;
                case AddAspect:
                    statement = connection.prepareStatement("INSERT INTO aspects (doc_id, asp_id, asp_type, txt, txt_len) VALUES (?, ?, ?, ?, ?)");
                    break;
                case AddCorpus:
                    statement = connection.prepareStatement("INSERT INTO corpora (corpus_name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                    break;
                case AddDocument:
                    statement = connection.prepareStatement("INSERT INTO documents DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS);
                    break;
                case AddDocumentCorpus:
                    statement = connection.prepareStatement("INSERT INTO document_corpus (corpus_id, doc_id) VALUES (?, ?)");
                    break;
                case AddProperty:
                    statement = connection.prepareStatement("INSERT INTO annotationproperties (ann_id, prop_key, ordnum, prop_value, namespace) VALUES (?, ?, ?, ?, ?)");
                    break;
                case AddTextScope:
                    statement = connection.prepareStatement("INSERT INTO textscopes (ann_id, doc_id, asp_id, span_start, span_end, ordnum, ref_type) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    break;
            }
        } catch (SQLException ex) {
            throw new ProcessingException("Could not create statement for " + statementId.toString(), ex);
        }
        preparedStatement.put(statementId, statement);
        return statement;
    }

    public int addCorpus(@SuppressWarnings("unused") /*XXX*/ Corpus corpus) throws ProcessingException {
        try {
            PreparedStatement statement = getPreparedStatement(PreparedStatementId.AddCorpus);
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            statement.setString(1, "Corpus stored at " + df.format(new Date()));
            return executeStatementAndGetId(statement);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not add new Corpus", ex);
        }
    }

    private int setAddPropStatementValues(PreparedStatement addPropertyStatement, int owningAnnotation_id, String propKey, int propNum, String propValue, String namespace) throws ProcessingException {
        try {
            addPropertyStatement.setInt(1, owningAnnotation_id);
            addPropertyStatement.setString(2, propKey);
            addPropertyStatement.setInt(3, propNum++);
            addPropertyStatement.setString(4, propValue);
            if (namespace == null || namespace.trim().isEmpty()) {
                addPropertyStatement.setNull(5, Types.VARCHAR);
            } else {
                addPropertyStatement.setString(5, namespace);
            }
            return propNum;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not valuate Annotation's Property", ex);
        }
    }

    private void addAnnotationProps(int owningAnnotation_id, Map<String, List<String>> features, String namespace) throws ProcessingException {
        try {
            boolean somePropsAdded = false;
            PreparedStatement addPropertyStatement = getPreparedStatement(PreparedStatementId.AddProperty);
            for (Entry<String, List<String>> featureEntry : features.entrySet()) {
                int propnum = 1;
                String key = featureEntry.getKey();
                for (String value : featureEntry.getValue()) {
                    propnum = setAddPropStatementValues(addPropertyStatement, owningAnnotation_id, key, propnum, value, namespace);
                    addPropertyStatement.addBatch();
                    somePropsAdded = true;
                }
            }
            if (somePropsAdded) {
                addPropertyStatement.executeBatch();
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not add Annotation's Properties", ex);
        }
    }

    private void setAddAnnotationStatementValues(PreparedStatement addAnnStatement, AnnotationKind kind, String annset_name, String namespace, String annotationType) throws ProcessingException {
        try {
            addAnnStatement.setString(1, kind.toString());
            addAnnStatement.setString(2, annset_name);
            addAnnStatement.setString(3, namespace);
            addAnnStatement.setString(4, annotationType);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not valuate Annotation", ex);
        }
    }

    private int addTextScope(int owningAnnotation_id, int doc_id, String asp_id, Integer spanStart, Integer spanEnd, int fragNum, String ref_type) throws ProcessingException {
        try {
            PreparedStatement addTextScopeStatement = getPreparedStatement(PreparedStatementId.AddTextScope);

            addTextScopeStatement.setInt(1, owningAnnotation_id);
            addTextScopeStatement.setInt(2, doc_id);
            if (asp_id == null) {
                addTextScopeStatement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                addTextScopeStatement.setString(3, asp_id);
            }
            if (spanStart == null) {
                addTextScopeStatement.setNull(4, java.sql.Types.INTEGER);
            }
            else {
                addTextScopeStatement.setInt(4, spanStart);
            }
            if (spanEnd == null) {
                addTextScopeStatement.setNull(5, java.sql.Types.INTEGER);
            }
            else {
                addTextScopeStatement.setInt(5, spanEnd);
            }
            addTextScopeStatement.setInt(6, fragNum);
            addTextScopeStatement.setString(7, ref_type);
            return executeStatementAndGetId(addTextScopeStatement);

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw new ProcessingException("Could not add TextScope", ex);
        }

    }
}
