function get_header_structure() {
    var top = {
	parent: null,
	node: null,
	level: 0,
	position: 0,
	children: []
    };
    var current = top;
    $('h2 , h3 , h4 , h5').not('.no-toc').each(function() {
	var level = Number(this.nodeName.substring(1));
	var p;
	for (p = current; p.level >= level; p = p.parent);
	current = {
	    parent: p,
	    node: this,
	    position: $(this).position().top,
	    level: level,
	    children: []
	};
	p.children.push(current)
    });
    return top;
}

function add_node(node, jqNode) {
    var tn = $('<li class="toc-item toc-level-'+node.level+'"><a href="#'+node.node.id+'">'+node.node.innerText+'</a></li>');
    node.toc_node = tn;
    jqNode.append(tn);
    add_children_nodes(node, jqNode);
}

function add_children_nodes(node, jqNode) {
    for (var i = 0; i < node.children.length; ++i) {
	add_node(node.children[i], jqNode);
    }
}

function get_scroll_header(position, node) {
    for (var i = node.children.length - 1; i >= 0; --i) {
	var n = node.children[i];
	var r = get_scroll_header(position, n);
	if (r) {
	    return r;
	}
    }
    if (node.position <= position) {
	return node;
    }
}

function toc_scroll(top) {
    var tocTop = $('#toc').offset().top;
    $(window).scroll(function() {
	var currentScroll = $(window).scrollTop();
	if (currentScroll >= tocTop) {
            $('#toc').css({
		position: 'fixed',
		top: '0',
            });
	}
	else {
            $('#toc').css({
		position: 'absolute',
		top: '150px',
            });
	}
	$('.toc-item').removeClass('toc-active');
	var n = get_scroll_header(currentScroll, top);
	if (n.toc_node !== undefined) {
	    n.toc_node.addClass('toc-active');
	}
    });
}

$(document).ready(function() {
    var top = get_header_structure();
    add_children_nodes(top, $('#toc ul'));
    toc_scroll(top);
    
});
