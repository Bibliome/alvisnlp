var theTree;

function getSelectedId() {
	var sel = theTree.getSelections();
	if (sel.length == 0) {
		return null;
	}
	var nodeId = sel[0];
	var info = nodeId.split('-');
	console.log(nodeId);
	return info[0];
}

function insertEvaluationNode(eltId, expr, parentNode) {
	$.get(
		'/api/treeview',
		{
			parentId: eltId + '-evaluate',
			expr: expr
		},
		function(data) {
			//console.log(data);
			theTree.addNode(
				{
					id: eltId + '-evaluation',
					text: '<span class="eval-node">'+expr+'</span>',
					hasChildren: false,
					imageHtml: '<img width="24" height="24" src="/res/icons/magnifier.png">',
					children: data,
					expanded: false
				},
				parentNode,
				2
			);
			//var newNode = theTree.getNodeById(eltId + '-evaluation');
			//theTree.expand(newNode);
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
	}
	else {
		insertEvaluationNode(eltId, expr, eltNode);
	}
	theTree.expand(eltNode);
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
    /*theTree.on('dataBound', function () {
        theTree.off('dataBound');
    });*/
    theTree.on('select', function() {
    	$('#btn-evaluate').removeClass('disabled');
    });
    theTree.on('unselect', function() {
    	$('#btn-evaluate').addClass('disabled');
    });
}

$(document).ready(function () {
	initTreeview();
});
