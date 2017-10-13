<h1 class="converter">AnnotationSet</h1>

## Synopsis

Annotation set specification for <a href="../module/ExportCadixeJSON" class="module">ExportCadixeJSON</a>.

## String conversion

String conversion is not available for this type.

## XML conversion

```xml
<annotationSet description="DESCRIPTION" id="ID" owner="OWNER" revision="REVISION" type="TYPE">
	  <text>
	    <properties>
	      <KEY>VALUE</KEY>
	      ...
	    </properties>
	    <instances>INSTANCES</instances>
	    <type>TYPE</type>
	  </text>
	  <group>
	    <properties>
	      <KEY>VALUE</KEY>
	      ...
	    </properties>
	    <instances>INSTANCES</instances>
	    <type>TYPE</type>
	    <items>ITEMS</items>
	  </group>
	  <relation>
	    <properties>
	      <KEY>VALUE</KEY>
	      ...
	    </properties>
	    <instances>INSTANCES</instances>
	    <type>TYPE</type>
	    <args>
	      <ROLE>ARG</ROLE>
	      ...
	    </args>
	  </relation>
	</annotationSet>
```




* *DESCRIPTION REVISION TYPE* are strings that specify respectively the description, the revision number and the type of the annotation set;
* *OWNER ID* are integers that specify respectively the owner identifier and the identifier of the annotation set (these are actually ignored by AlvisAE import);
* *KEY* is a string that specify an annotation property key;
* *VALUE* is an expression evaluated as a string with the instance element as the context element, the result specifies the annotation property value;
* *INSTANCES* is an expression evaluated as a list of elements with the document as the context element that specifies all instances of the annotation type;
* *TYPE* is an expression evaluated as a string with the instance element as the context element that specifies the type of annotations;
* *ITEMS* is an expression evaluated as a list of elements with the instance element as the context element that specifies all items in the group, the result elements must have been instances in the same annotation set;
* *ROLE* is a string that specifies a relation argument role label;
* *ARG* is an expression evaluated as a list of elements with the instance element as the context element that specifies the argument of the relation (only the first taken into account), the argument must have been instances in the same annotatio set;



