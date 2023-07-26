SECTION_CARD_PROPERTY = {
  name: 'Section',
  value: function(ment) { return ment.container.name; }
};

SECTION_TABLE_COLUMN = {
  name: 'Section',
  content: function(ment, index) { return ment.container.name; },
  type: String
};

function linkCardProperty(fcg) {
  return {
    name: 'Link',
    value: function(ment) {
      const urlPrefix = location.href.replace(location.search, '');
      const params = new URLSearchParams({
        doc: document.querySelector('.doc.selected').textContent,
        ment: fcg.mentions.indexOf(ment)
      });
      const url = urlPrefix + '?' + params;
      return '<a href="' + url + '">link</a>';
    }
  };
}

function featureCard(feature) {
  return MentionCardBuilder.autoLink({
    name: feature.charAt(0).toUpperCase() + feature.slice(1),
    value: function(ment) { return ment.data[feature]; }
  });
}

function getDocElt(doc) {
  if (doc instanceof Event) {
    return event.target;
  }
  if (doc instanceof Element) {
    return doc;
  }
  if (typeof doc === 'string') {
    const docDiv = document.querySelector('div#docs');
    for (let docElt of docDiv.children) {
      if (docElt.textContent == doc) {
        return docElt;
      }
    }
  }
  throw new Error('could not find document: ' + doc);
}

function selectDocument(doc, selMent) {
  const docElt = getDocElt(doc);
  if (docElt.classList.contains('selected')) {
    return;
  }
  document.querySelectorAll('.doc.selected').forEach(e => e.classList.remove('selected'));
  docElt.classList.add('selected');

  const fca = [];
  for (let sec of docElt.sections) {
    const fc = new FragmentContainer(sec.name + ' (' + sec.ord + ')', sec.text);
    sec.layouts.forEach(lay => fc.addLayout(...lay));
    sec.mentions.forEach(ment => fc.addMention(...ment.ctor).data = ment.data);
    fca.push(fc);
  }
  const fcg = new FragmentContainerGroup(...fca);

  const colors = document.quickHTMLColors || Colors.spread();
  fragmentStyler = new FragmentBoxColorMentionStyler({colors: colors});
  tableStyler = new TableColorMentionStyler({colors: colors});
  cardStyler = new MentionCardStyler({
    rawCSS: [
      'td.prop-value { font-family: monospace; }'
    ],
    colors: colors
  });

  const fragsElt = document.querySelector('#frags');
  fragsElt.innerHTML = '';
  for (let fc of fca) {
    const h1 = Builder.createElement('H1', fc.name, fragsElt);
    h1.classList.add('section-title');
    const div = Builder.createElement('DIV', null, fragsElt);
    div.classList.add('section-content')
    fc.addBuilder(div, FragmentBuilder, fragmentStyler);
  }
  fcg.addBuilder('#table', TableBuilder, tableStyler, [
    TableBuilder.COLUMN_INDEX,
    SECTION_TABLE_COLUMN,
    TableBuilder.COLUMN_TYPE,
    TableBuilder.COLUMN_FORM
  ]);
  fcg.addBuilder('#card', MentionCardBuilder, cardStyler,
    [linkCardProperty(fcg), SECTION_CARD_PROPERTY].concat(document.quickHTMLFeatures.map(featureCard))
  );
  fcg.build();

  if ((selMent !== null) && (selMent !== undefined)) {
    const mentions = fcg.mentions;
    if (selMent < mentions.length) {
      const ment = mentions[selMent];
      fcg.highlight(ment);
    }
  }
  else {
    const mentions = fcg.mentions;
    if (mentions.length) {
      fcg.highlight(mentions[0]);
    }
  }

  function hl(event) {
    const mention = Mention.from(event);
    if (mention) {
      fcg.highlight(mention);
    }
  }
  fcg.allBuilders.forEach(b => b.buildRoot.onclick = hl);
}

function loadDocuments(data) {
  const docDiv = document.querySelector('div#docs');
  let firstDoc = null;
  for (let doc of data.documents) {
    const docElt = document.createElement('DIV');
    docElt.classList.add('doc');
    docElt.innerHTML = doc.title;
    docElt.sections = doc.sections;
    docElt.onclick = selectDocument;
    docDiv.appendChild(docElt);
    if (firstDoc === null) {
      firstDoc = docElt;
    }
  }
  document.quickHTMLColors = data.colors;
  document.quickHTMLFeatures = data.features;

  const urlParams = new URLSearchParams(window.location.search);
  const doc = urlParams.get('doc');
  if (doc) {
    const ment = urlParams.get('ment');
    selectDocument(doc, ment);
  }
  else if (firstDoc) {
    selectDocument(firstDoc);
  }
}
