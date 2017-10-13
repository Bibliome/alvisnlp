<h1 class="module">WebOfKnowledgeReader</h1>

## Synopsis

Reads [Web of Knowledge](http://apps.webofknowledge.com/UA_GeneralSearch_input.do?product=UA&search_mode=GeneralSearch) search result import files.

## Description

**WARNING:** WoK delivers files with a wrong [Byte Order Mark](https://en.wikipedia.org/wiki/Byte_order_mark), it is advised you remove it using a text editor before feeding it to *WebOfKnowledgeReader*.

The PT field (Publication Type) is used as a document marker, *WebOfKnowledgeReader* will create a document each time it reads a PT field.

The VR field will be read and, if its value is different from "1.0", then *WebOfKnowledgeReader* fails.

The following fields will be read and stored as document features, one feature per line: AU, AF, BA, BF, CA, GP, BE, SO, SE, BS, LA, CT, CY, CL, SP, HO, C1, RP, EM, RI, OI, FU, CR, TC, Z9, PU, PI, PA, SN, BN, J9, JI, PD, PY, VL, IS, PN, SU, MA, BP, EP, AR, DI, D2, PG, P2, GA, UT, SI, NR.

The following fields will be read and stored as document features, several features per line split with semicolons: DE, DT, ID, WC, SC.

The following fields will be read and stored as sections, all lines concatenated for the contents: TI, AB, FX.

The following fields will be ignored: ER, EF, FN.

The feature and section names are the 2-character field code. For an interpretation of field codes, see [WoK format documentation](http://images.webofknowledge.com/WOKRS510B3_1/help/WOS/hs_wos_fieldtags.html).

## Parameters

<a name="source">

### source

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.util.streams.SourceStream" class="converter">SourceStream</a>
</div>
Location of the WoK file(s).

<a name="constantDocumentFeatures">

### constantDocumentFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each document created by this module.

<a name="constantSectionFeatures">

### constantSectionFeatures

<div class="param-level param-level-optional">Optional
</div>
<div class="param-type">Type: <a href="../converter/fr.inra.maiage.bibliome.alvisnlp.core.module.types.Mapping" class="converter">Mapping</a>
</div>
Constant features to add to each section created by this module.

<a name="tabularFormat">

### tabularFormat

<div class="param-level param-level-default-value">Default value: `false`
</div>
<div class="param-type">Type: <a href="../converter/java.lang.Boolean" class="converter">Boolean</a>
</div>
Read files in tabular export format.

