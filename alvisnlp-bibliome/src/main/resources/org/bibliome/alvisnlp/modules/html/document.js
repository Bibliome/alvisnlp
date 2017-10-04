var start = function() {
	$(window).keydown(function(event) {
		switch (event.which) {
		case 65: followPrev(); break;
		case 68: followNext(); break;
//		default: console.log(event);
		}
	});
	
	$('.alvisnlp-fragment').click(function(event) {
		event.stopPropagation();
		highlight(this);
	});
	
	highlight($('.alvisnlp-fragment').get(0));
	
	$('#annotation-next').click(followNext);
	$('#annotation-prev').click(followPrev);
};

var follow = function(attr) {
	return function() {
		var id = $('.alvisnlp-first-fragment.highlight').attr(attr);
		if (id) {
			var e = $('.alvisnlp-first-fragment[alvisnlp-id='+id+']').get(0);
			highlight(e);
		}
	};
}

var followNext = follow('alvisnlp-next');
var followPrev = follow('alvisnlp-prev');

var highlight = function(elt) {
	if (elt === undefined) {
		return;
	}
	$('.highlight').removeClass('highlight');
	var aid = elt.getAttribute('alvisnlp-id');
	var allFragments = $('*[alvisnlp-id="'+aid+'"]');
	allFragments.addClass('highlight');
	
	var featuresDiv = $('#annotation-features');
	featuresDiv.empty();
	var firstFragment = allFragments.first();
	var keys = firstFragment.attr('alvisnlp-feature-keys').split(' ');
	keys.forEach(function(key) {
		var value = firstFragment.attr('alvisnlp-feature-value-' + key);
		featuresDiv.append('<div class="alvisnlp-feature"><span class="alvisnlp-feature-key">'+key+'</span><span class="alvisnlp-feature-value">'+value+'</span></div>');
	});

	window.scrollTo(0, $(elt).position().top);
};
