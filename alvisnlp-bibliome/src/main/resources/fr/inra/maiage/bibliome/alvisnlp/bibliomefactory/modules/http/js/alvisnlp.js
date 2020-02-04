var theTree;
var currentContentDocId = '';
var layers = {};
var palette = [
	'#5899DA',
	'#E8743B',
	'#19A979',
	'#ED4A7B',
	'#945ECF',
	'#13A4B4',
	'#525DF4',
	'#BF399E',
	'#6C8893',
	'#EE6868',
	'#2F6497'
];
var nextColorIndex = 0;
var layerColors = {};
var currentCorpusId;

function showAlert(level, message) {
	$('#pane-alert').append(
	    $('<div class="alert alert-dismissible" role="alert">')
    		.addClass('alert-'+level)
        	.append(
	        	$('<span  style="margin-right: 5mm"></span>').append(message),
    	        //$('<button type="button" class="close" data-dismiss="alert"></button>').append('<span>&times;</span>')
        	)
	        .fadeTo(5000, 1, function() { $(this).fadeOut(500); })
    );
}

function getSelectedId() {
	var sel = theTree.getSelections();
	if (sel.length == 0) {
		return null;
	}
	var nodeId = sel[0];
	var info = nodeId.split('-');
	//console.log(nodeId);
	return info[0];
}

function insertEvaluationNode(eltId, expr, parentNode) {
	$.get(
		'/api/treeview',
		{
			parentId: eltId + '-evaluate',
			expr: expr
		})
		.done(
			function(data) {
				//console.log(data);
				theTree.addNode(
					{
						id: eltId + '-evaluation',
						text: '<span class="tree-node eval-node">'+expr+'</span>',
						hasChildren: false,
						imageHtml: '<img width="24" height="24" src="/res/icons/gear.png">',
						children: data,
						expanded: false
					},
					parentNode,
					2
				);
				//var newNode = theTree.getNodeById(eltId + '-evaluation');
				//theTree.expand(newNode);
				$('#expression').val('');
			})
		.fail(
			function(data) {
				showAlert('danger', data.responseText);
				console.error(data);
			}
		);
}

function evaluateExpression() {
	//checkCurrentCorpusId();
	var eltId = getSelectedId();
	var expr = $('#expression').val();
	if (expr == '') {
		return;
	}
	var eltNode = theTree.getNodeById(eltId + '-children');
	var eltData = theTree.getDataById(eltId + '-children');
	if (eltData.children === undefined) {
		theTree.on('dataBound', function(e) {
			insertEvaluationNode(eltId, expr, eltNode);
			theTree.off('dataBound');
		});
		theTree.expand(eltNode);
	}
	else {
		insertEvaluationNode(eltId, expr, eltNode);
		theTree.expand(eltNode);
	}
}

function showAndExpandNode(nodeId) {
	var data = theTree.getDataById(nodeId);
	if (data.hasChildren) {
		var node = theTree.getNodeById(nodeId);
		node[0].scrollIntoViewIfNeeded({behaviour: 'smooth'});
		theTree.expand(node);
	}
}

function showAndExpandNodeArray(nodeIds, andSelect) {
	//checkCurrentCorpusId();
	var nodeId;
	theTree.on('dataBound', function(e) {
		while (nodeIds.length > 0) {
			//console.log(nodeIds);
			nodeId = nodeIds.shift();
			var data = theTree.getDataById(nodeId);
			var end = (data.children === undefined);
			showAndExpandNode(nodeId);
			if (end) {
				break;
			}
		}
		//console.log(nodeIds);
		if (nodeIds.length == 0) {
			theTree.off('dataBound');
			if (andSelect) {
				var node = theTree.getNodeById(nodeId);
				theTree.unselectAll();
				theTree.select(node);
			}
		}
	});
	theTree.trigger('dataBound');
}

function focusFrags(frags, event, showInTree) {
	if (showInTree) {
		checkCurrentCorpusId();
	}
	//console.log(event);
	//console.log(frags);
	$('.frag.frag-focus').removeClass('frag-focus');
	for (frag of frags) {
		var eltId = $(frag).data('eltid');
		var layer = $(frag).data('layer');
		//console.log(eltId);
		//console.log(layer);
		//console.log($(frag).data());
		$('.frag[data-eltid="'+eltId+'"][data-layer="'+layer+'"]').addClass('frag-focus');
		if (showInTree) {
			var docEltId = $(frag).parents('div.doc-container').data('eltid');
			var secEltId = $(frag).parents('div.sec-container').data('eltid');
			showAndExpandNodeArray([docEltId + '-children', secEltId + '-children', secEltId + '-annotations-' + layer, eltId + '-children'], true);
		}
	}
	if (event !== undefined) {
		event.stopPropagation();
	}
}

function getNextColor() {
	var result = palette[nextColorIndex];
	nextColorIndex = (nextColorIndex + 1) % palette.length;
	return result;
}

function updateLayers(docLayers) {
	//checkCurrentCorpusId();
	for (l in layers) {
		if (layers.hasOwnProperty(l)) {
			layers[l] = $('#check-'+l).prop('checked');
		}
	}
	for (l of docLayers) {
		if (!layers.hasOwnProperty(l)) {
			layers[l] = false;
		}
	}
	$('#layer-names .col-2').remove();
	for (l in layers) {
		if (layers.hasOwnProperty(l)) {
			var checked = '';
			var moreClasses= '';
			if (layers[l]) {
				checked = 'checked';
				moreClasses = 'frag frag-notempty frag-left-closed frag-right-closed frag-nesting-0 layer-'+l;
				if (!(l in layerColors)) {
					var c = getNextColor();
					layerColors[l] = c;
					document.adoptedStyleSheets[0].insertRule('.layer-'+l+' { background-image: radial-gradient(ellipse, white, white, '+c+'); }');
				}
			}
			$('#layer-names').append('<div class="custom-control custom-checkbox col-2"><input type="checkbox" '+checked+' class="custom-control-input" id="check-'+l+'" onChange="checkCurrentCorpusId(); updateContentView(currentContentDocId, []); updateLayers([])"><label class="custom-control-label '+moreClasses+'" for="check-'+l+'"><img src="/res/icons/tags-label.png" height="24" width="24" class="layer-icon" alt="Layer">'+l+'</label></div>');
		}
	}
}

function getCheckedLayers() {
	var result = []; 
	for (l in layers) {
		if (layers.hasOwnProperty(l)) {
			if ($('#check-'+l).prop('checked')) {
				result.push(l);
			}
		}
	}
	return result;
}

function updateContentView(docId, annotationIds) {
	//console.log(docId);
	//console.log(annotationIds);
	//checkCurrentCorpusId();
	$.get(
		'/api/contentview',
		{
			docId: docId,
			'layers[]': getCheckedLayers()
		})
	.done(function(data) {
		//console.log(data);
		$('.div-top').remove();
		$('#content-view').append(data);
		currentContentDocId = docId;
		focusFrags(annotationIds.map(function(id) { return $('.frag[data-eltid="'+id+'"]'); }), undefined, false);
	})
	.fail(function(data) {
		console.error(data);
	});
}

function initTreeview() {
	theTree = $('#tree').tree({
		uiLibrary: 'bootstrap4',
		//iconsLibrary: 'glyphicons',
		//iconsLibrary: 'materialicons',
        dataSource: '/api/treeview',
        primaryKey: 'id',
        hasChildrenField: 'hasChildren',
        lazyLoading: true
    });
    theTree.on('select', function(e, node, id) {
		checkCurrentCorpusId();
    	$('#btn-evaluate').removeClass('disabled');
		var info = id.split('-');
		var eltId = info[0];
		$.get(
			'/api/info',
			{
				eltId: eltId
			})
		.done(function(data) {
			if (data['found-doc']) {
				if (data.id != currentContentDocId) {
					updateLayers(data.layers);
					updateContentView(data.id, data.annotations);
				}
				else {
					//console.log(data.annotations);
					focusFrags(data.annotations.map(function(id) { return $('.frag[data-eltid="'+id+'"]'); }), undefined, false);
				}
			}
		})
		.fail(function(data) {
			console.error(data);
		});
    });
    theTree.on('unselect', function() {
    	$('#btn-evaluate').addClass('disabled');
    });
}

function focusExpression() {
	checkCurrentCorpusId();
	var sel = theTree.getSelections();
	if (sel.length == 0) {
		return;
	}
	var nodeId = sel[0];
	var info = nodeId.split('-');
	var eltId = info[0];
	var params = {
		eltId: eltId
	};
	if (info[1] == 'annotations') {
		params.layer = info[2];
	}
	$.get(
		'/api/defaultexpr',
		params
	).done(function(data) {
		$('#expression').val(data);
	})
}

function checkCurrentCorpusId() {
	console.log(currentCorpusId);
	$.get(
		'/api/corpus'
	).done(function(data) {
		var id = data[0]['UID'];
		if (currentCorpusId === undefined) {
			currentCorpusId = id;
			return;
		}
		if (currentCorpusId != id) {
			showAlert('warning', 'Reloading...');
			window.location.reload(true);
		}
	});
}

$(document).ready(function () {
	initTreeview();
	$('#expression').on('focus', focusExpression);
	document.adoptedStyleSheets = [ new CSSStyleSheet() ];
});
