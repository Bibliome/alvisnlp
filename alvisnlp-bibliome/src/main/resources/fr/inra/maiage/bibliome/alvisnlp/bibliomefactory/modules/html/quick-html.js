function selectDocument(event) {
  const docElt = event.target;
  if (docElt.classList.contains('selected')) {
    return;
  }
  document.querySelectorAll('.doc.selected').forEach(e => e.classList.remove('selected'));
  docElt.classList.add('selected');

  const fca = [];
  for (let sec of docElt.sections) {
    const fc = new FragmentContainer(sec.name, sec.text);
    sec.layouts.forEach(lay => fc.addLayout(...lay));
    sec.mentions.forEach(ment => fc.addMention(...ment.ctor).data = ment.data);
    fca.push(fc);
  }
  const fcg = new FragmentContainerGroup(...fca);

  let colorOptions;
  if (document.quickHTMLColors === null) {
    colorOptions = { mentionTypeColorFactory: { method: 'spread' } }
  }
  else {
    colorOptions = { mentionTypeColors: document.quickHTMLColors }
  }
  fragmentStyler = new FragmentHighlightColorMentionStyler(fcg, colorOptions);
  tableStyler = new TableHighlightColorMentionStyler(fcg, Object.assign({
    rawCSS: [
      'td.N { font-family: monospace; text-align: right; }',
      'td.Type { font-family: monospace; }',
      'td.Form { font-style: italic; }'
    ]
  }, colorOptions));
  cardStyler = new MentionCardStyler(fcg, colorOptions);

  const fragsElt = document.querySelector('#frags');
  fragsElt.innerHTML = '';
  for (let fc of fca) {
    const name = fc.name.charAt(0).toUpperCase() + fc.name.slice(1);
    const h1 = Builder.createElement('H1', name, fragsElt);
    h1.classList.add('section-title');
    const div = Builder.createElement('DIV', null, fragsElt);
    div.classList.add('section-content')
    fc.addBuilder(div, FragmentBuilder, fragmentStyler);
  }
  fcg.addBuilder('#table', TableBuilder, tableStyler, [
    TableBuilder.COLUMN_INDEX,
    TableBuilder.COLUMN_TYPE,
    TableBuilder.COLUMN_FORM
  ]);
  fcg.addBuilder('#card', MentionCardBuilder, cardStyler, [MentionCardBuilder.PROPERTY_TYPE]);
  fcg.build();

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
  for (let doc of data.documents) {
    const docElt = document.createElement('DIV');
    docElt.classList.add('doc');
    docElt.innerHTML = doc.id;
    docElt.sections = doc.sections;
    docElt.onclick = selectDocument;
    docDiv.appendChild(docElt);
  }
  document.quickHTMLColors = data.colors;
  document.quickHTMLFeatures = data.features;
}
