function getAPIURL(ftor, uid, args) {
	var url = '/api/' + ftor + '?uid=' + uid;
	if (args !== undefined) {
		for (key of Object.keys(args)) {
			url = url + '&' + key + '=' + args[key];
		}
	}
	console.log(url);
	return url;
}

function showElements(url) {
	$.ajax({
		url: url,
        error: function(xhr, status, error) {
            console.log(error);
        },
        success: function(data, status, xhr) {
        	var tbody = $('#result tbody');
        	tbody.empty();
        	for (var i = 0; i < data.length; i++) {
        		var row = elementRow(data[i]);
        		tbody.append(row);
        	}
        }
	});
}

class ElementRow {
	static document(row, elt) {
		row.append($('<td>'+elt.id+'</td>'));
		row.append($('<td>[<a onclick="showElements(\''+getAPIURL('sections', elt.UID)+'\')">sections</a>]</td>'))
	}
	
	static section(row, elt) {
		row.append($('<td>'+elt.name+'/'+elt.order+'</td>'));
		row.append($('<td>[<a onclick="showElements(\''+getAPIURL('layers', elt.UID)+'\')">layers</a>]</td>'))
	}
	
	static layer(row, elt) {
		row.append($('<td>'+elt.name+'</td>'));
		row.append($('<td>[<a onclick="showElements(\''+getAPIURL('annotations', elt.section, {'layer': elt.name})+'\')">annotations</a>]</td>'))
	}
	
	static annotation(row, elt) {
		row.append($('<td>'+elt.form+'</td>'));
		row.append($('<td>'+elt.start+'-'+elt.end+'</td>'));
	}
}

function elementRow(elt) {
	var row = $('<tr>');
	row.append($('<td>'+elt.type+'</td>'));
	var fun = ElementRow[elt.type];
	if (fun === undefined) {
		console.log(elt);
	}
	else {
		//console.log(elt);
		fun(row, elt);
	}
	return row;
}

$(document).ready(function() {
	showElements('/api/documents');
}
);
