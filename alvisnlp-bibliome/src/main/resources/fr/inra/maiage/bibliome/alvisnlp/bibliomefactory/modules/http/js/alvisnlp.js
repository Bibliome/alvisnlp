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
	var nodeId;
	theTree.on('dataBound', function(e) {
		while (nodeIds.length > 0) {
			console.log(nodeIds);
			nodeId = nodeIds.shift();
			var data = theTree.getDataById(nodeId);
			var end = (data.children === undefined);
			showAndExpandNode(nodeId);
			if (end) {
				break;
			}
		}
		console.log(nodeIds);
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

function focusFrag(event, frag) {
	//console.log(event);
	$('.frag.frag-focus').removeClass('frag-focus');
	var eltId = $(frag).data('eltid');
	var layer = $(frag).data('layer');
	//console.log(eltId);
	//console.log(layer);
	//console.log($(frag).data());
	$('.frag[data-eltId="'+eltId+'"][data-layer="'+layer+'"]').addClass('frag-focus');

	var docEltId = $(frag).parents('div.doc-container').data('eltid');
	var secEltId = $(frag).parents('div.sec-container').data('eltid');
	showAndExpandNodeArray([docEltId + '-children', secEltId + '-children', secEltId + '-annotations-' + layer, eltId + '-children'], true);
		
	event.stopPropagation();
}

function getNextColor() {
	var result = palette[nextColorIndex];
	nextColorIndex = (nextColorIndex + 1) % palette.length;
	return result;
}

function updateLayers(docLayers) {
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
			$('#layer-names').append('<div class="custom-control custom-checkbox col-2"><input type="checkbox" '+checked+' class="custom-control-input" id="check-'+l+'" onChange="updateContentView(currentContentDocId); updateLayers([])"><label class="custom-control-label '+moreClasses+'" for="check-'+l+'"><img src="/res/icons/tags-label.png" height="24" width="24" class="layer-icon" alt="Layer">'+l+'</label></div>');
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

function updateContentView(docId) {
	//console.log(docId);
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
    	$('#btn-evaluate').removeClass('disabled');
		var info = id.split('-');
		var eltId = info[0];
		$.get(
			'/api/docinfo',
			{
				eltId: eltId
			})
		.done(function(data) {
			if (data.found && (data.id != currentContentDocId)) {
				updateLayers(data.layers);
				updateContentView(data.id);
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

$(document).ready(function () {
	initTreeview();
	document.adoptedStyleSheets = [ new CSSStyleSheet() ];
});
