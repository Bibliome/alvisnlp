function featureCard(feature) {
  return {
    name: feature.charAt(0).toUpperCase() + feature.slice(1),
    value: function(ment) { return ment.data[feature]; }
  };
}

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
  fcg.addBuilder('#card', MentionCardBuilder, cardStyler,
    document.quickHTMLFeatures.map(featureCard)
  );
  fcg.build();

  function hl(event) {
    const mention = Mention.from(event);
    if (mention) {
      fcg.highlight(mention);
    }
  }
  fcg.allBuilders.forEach(b => b.buildRoot.onclick = hl);

  const mentions = fcg.mentions;
  if (mentions.length) {
    fcg.highlight(mentions[0]);
  }
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
