
class ElementFace {
	static corpus(_) {
		return '';
	}
	
	static document(doc) {
		return doc.id;
	}
	
	static section(sec) {
		return sec.name + ' (' + sec.order + ')';
	}
}

class ElementButtons {
	static _button(item, onclick, btn) {
		item.append('<a class="element-button" href="#" onclick="'+onclick+'">[+'+btn+']</a>');
	}
	
	static corpus(item, elt) {
		ElementButtons._button(item, 'showDocuments(\''+item.attr('id')+'\')', 'docs');
	}
	
	static document(item, elt) {
		ElementButtons._button(item, 'showSections(\''+item.attr('id')+'\')', 'secs');
	}
	
	static section(item, elt) {
		
	}
}

function createElementItem(elt) {
	var item = $('<li></li>');
	item.attr('id', 'elt-' + elt.UID);
	item.append('<span class="element-type">'+elt.type+'</span>');
	item.append('<span class="element-face">'+ElementFace[elt.type](elt)+'</span>');
	ElementButtons[elt.type](item, elt);
	item.append('<ul></ul>');
	return item;
}

function showCorpus() {
	$.ajax({
		url: '/api/corpus',
        error: function(xhr, status, error) {
            console.log(error);
        },
        success: function(data, status, xhr) {
        	$('#result ul').append(createElementItem(data[0]));
        }
	});
}

function showDocuments(id) {
	console.log(id);
	var ul = $('#'+id+' > ul');
	console.log(ul);
	if (ul.is(':empty')) {
		$.ajax({
			url: '/api/documents',
			error: function(xhr, status, error) {
				console.log(error);
			},
			success: function(data, status, xhr) {
				ul.empty();
				for (var elt of data) {
					ul.append(createElementItem(elt));
				}
			}
		});
	}
	else {
		ul.empty();
	}
}

function showSections(id) {
	console.log(id);
	var ul = $('#'+id+' > ul');
	console.log(ul);
	if (ul.is(':empty')) {
		$.ajax({
			url: '/api/sections?uid=' + id.substring(4),
			error: function(xhr, status, error) {
				console.log(error);
			},
			success: function(data, status, xhr) {
				ul.empty();
				for (var elt of data) {
					ul.append(createElementItem(elt));
				}
			}
		});
	}
	else {
		ul.empty();
	}
}

$(document).ready(function() {
	showCorpus();
}
);
