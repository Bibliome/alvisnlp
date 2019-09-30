var theTree;
var currentContentDocId = '';
var foo = null;

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

function updateContentView(docId) {
	console.log(docId);
	$.get(
		'/api/contentview',
		{
			docId: docId
		})
	.done(function(data) {
		console.log(data);
		$('.div-top').remove();
		$('#pane-right').append(data);
		currentContentDocId = docId;
		foo = data;
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
			'/api/doc-of',
			{
				eltId: eltId
			})
		.done(function(data) {
			if ((data != '') && (data != currentContentDocId)) {
				updateContentView(data);
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
});
