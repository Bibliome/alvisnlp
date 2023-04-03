"use strict";

function _createId(obj) {
  const url = URL.createObjectURL(new Blob([obj]));
  return url.substring(url.lastIndexOf('/') + 1);
}

class Fragment {
  constructor(container, fragmentOrder, tag, start, end, options={}) {
    console.assert(container, 'Fragment should be created within a container');
    console.assert(start <= end, 'start > end');
    console.assert(end <= container.text.length, 'end > length');
    this.container = container;
    this.fragmentOrder = fragmentOrder;
    this.tag = tag;
    this.start = start;
    this.end = end;
    this.order = options.order || 1;
    this.attributes = Object.assign({}, options.attributes || {});
    this.class = this._getClass(options.class);
    this.typeOrder = 0;
  }

  _getClass(class_) {
    if (!class_) {
      return [];
    }
    if (typeof class_ === 'string') {
      return [class_];
    }
    return class_.slice();
  }

  get isMention() {
    throw new Error('Not Implemented');
  }

  get isEmpty() {
    return this.start === this.end;
  }

  get form() {
    return this.container.text.substring(this.start, this.end);
  }

  createElement() {
    const result = document.createElement(this.tag);
    for (let a in this.attributes) {
      result.setAttribute(a, this.attributes[a]);
    }
    result.classList.add(...this.class);
    return result;
  }
}

class Layout extends Fragment {
  constructor(container, tag, start, end, options) {
    super(container, Layout.isBlock(tag, options) ? 2 : 4, tag, start, end, options);
  }

  get isMention() {
    return false;
  }

  static isBlock(tag, options) {
    return options.block ||
     tag.startsWith('h') ||
     tag === 'div' ||
     tag === 'p';
  }
}

class Mention extends Fragment {
  constructor(container, type, start, end, options) {
    super(container, 3, "SPAN", start, end, options);
    this.type = type;
    this.data = options.data || {};
    this.level = null;
    this.id = options.id || _createId(this);
  }

  static compare(a, b) {
    if (a.start !== b.start) {
      return a.start - b.start;
    }
    if (a.end !== b.end) {
      return b.end - a.end;
    }
    if (a.typeOrder !== b.typeOrder) {
      return a.typeOrder - b.typeOrder;
    }
    if (a.order !== b.order) {
      return a.order - b.order;
    }
    return 0;
  }

  createElement() {
    const result = super.createElement();
    this.stamp(result);
    return result;
  }

  stamp(element) {
    element.classList.add(this.type, 'mention', 'mention-' + this.id);
    element.mention = this;
  }

  get isMention() {
    return true;
  }

  static from(obj) {
    if (obj instanceof Mention) {
      return key;
    }
    if (obj instanceof Element) {
      for (let elt = obj; elt; elt = elt.parentElement) {
        if (elt.mention) {
          return elt.mention;
        }
      }
      return undefined;
    }
    if (obj instanceof Event) {
      return Mention.from(obj.target);
    }
  }
}

class FragmentBuildable {
  constructor() {
    this.builders = [];
  }

  get mentions() {
    throw new Error('Not Implemented');
  }

  get mentionTypes() {
    throw new Error('Not Implemented');
  }

  get maxLevel() {
    throw new Error('Not Implemented');
  }

  addBuilder(targetElement, builderClass, styler, ...args) {
    const builder = new builderClass(this, targetElement, styler, ...args);
    this.builders.push(builder);
    return builder;
  }

  build() {
    this.builders.forEach(b => b.build());
  }

  clearHighlight() {
    this.builders.forEach(b => b.clearHighlight());
  }

  highlight(mention, clear=true) {
    if (clear) {
      this.clearHighlight();
    }
    this.builders.forEach(b => b.highlight(mention));
  }
}

class FragmentContainerGroup extends FragmentBuildable {
  constructor(...fragmentContainers) {
    super();
    this.fragmentContainers = fragmentContainers;
  }

  get allBuilders() {
    let result = this.builders;
    for (let fc of this.fragmentContainers) {
      result = result.concat(fc.builders);
    }
    return result;
  }

  get mentions() {
    let result = [];
    for (let fc of this.fragmentContainers) {
      result = result.concat(fc.mentions);
    }
    return result;
  }

  get mentionTypes() {
    let result = [];
    for (let fc of this.fragmentContainers) {
      result = result.concat(fc.mentionTypes);
    }
    return result;
  }

  get maxLevel() {
    return Math.max(...this.fragmentContainers.map(fc => fc.maxLevel));
  }

  build() {
    super.build();
    this.fragmentContainers.forEach(fc => fc.build());
  }

  clearHighlight() {
    super.clearHighlight();
    this.fragmentContainers.forEach(fc => fc.clearHighlight());
  }

  highlight(mention, clear=true) {
    super.highlight(mention, clear);
    this.fragmentContainers.forEach(fc => fc.highlight(mention, clear));
  }
}

class FragmentContainer extends FragmentBuildable {
  constructor(name, text) {
    super();
    this.name = name;
    this.text = text;
    this.layouts = [];
    this._mentions = [];
    this.mentionTypeMap = {};
    this.id = 'FragmentContainer-' + _createId(this);
    this._maxLevel = 0;
  }

  __traverseChildren(element) {
    for (const child of element.childNodes) {
      switch (child.nodeType) {
        case Node.ELEMENT_NODE: {
          const start = this.text.length;
          this._traverseChildren(child);
          const end = this.text.length;
          this.addLayout(child.tagName, start, end, { attributes: Object.fromEntries(child.attributes) });
          break;
        }
        case Node.TEXT_NODE: {
          this.text += child.nodeValue;
        }
      }
    }
  }

  addLayout(tag, start, end, options={}) {
    const layout = new Layout(this, tag, start, end, options);
    this.layouts.push(layout);
    return layout;
  }

  get mentions() {
    this._mentions.sort(Mention.compare);
    return this._mentions;
  }

  addMention(type, start, end, options={}) {
    console.assert(end <= this.text.length, 'end > length');
    const mention = new Mention(this, type, start, end, options);
    this.mentions.push(mention);
    if (!(type in this.mentionTypeMap)) {
      this.mentionTypeMap[type] = Object.values(this.mentionTypeMap).length + 1;
    }
    mention.typeOrder = this.mentionTypeMap[type];
    this._maxLevel = null;
    return mention;
  }

  get mentionTypes() {
    return Object.keys(this.mentionTypeMap);
  }

  get fragments() {
    return this.layouts.concat(this.mentions);
  }

  updateLevels() {
    if (this._maxLevel !== null) {
      return;
    }
    let mentions = this.mentions;
    mentions.forEach(ment => ment.level = null);
    for (let lvl = 0; mentions.length; ++lvl) {
      let prev = null;
      for (let ment of mentions) {
        if ((prev !== null) && (ment.start >= prev.end)) {
          prev.level = lvl;
        }
        prev = ment;
      }
      prev.level = lvl;
      mentions = mentions.filter(ment => ment.level === null);
    }
    this._maxLevel = Math.max(...this.mentions.map(ment => ment.level));
  }

  get maxLevel() {
    this.updateLevels();
    return this._maxLevel;
  }
}

class Builder {
  constructor(fragmentContainer, targetElement, styler) {
    this.fragmentContainer = fragmentContainer;
    if (typeof targetElement === 'string') {
      this.targetElement = document.querySelector(targetElement);
    }
    else {
      this.targetElement = targetElement;
    }
    if (this.targetElement.shadowRoot === null) {
      this.targetElement.attachShadow({mode: 'open'});
    }
    this.id = 'builder-' + _createId();
    this.targetElement.classList.add(this.id);
    this.styler = styler;
    this.buildRoot = null;
    this.highlighted = [];
  }

  build() {
    const shadowRoot = this.targetElement.shadowRoot;
    const df = document.createDocumentFragment();
    if (this.styler) {
      const style = this.styler.createStyle();
      df.appendChild(style);
    }
    this.buildRoot = this.doBuild();
    df.appendChild(this.buildRoot);
    shadowRoot.replaceChildren(df);
  }

  clearHighlight() {
    this.highlighted = [];
  }

  highlight(mention) {
    this.highlighted.push(mention);
  }

  static createElement(tag, content, parent) {
    const result = document.createElement(tag);
    if (content) {
      if (typeof content === 'string') {
        result.innerHTML = content;
      }
      else {
        result.appendChild(content);
      }
    }
    if (parent) {
      parent.appendChild(result);
    }
    return result;
  }
}

class MentionCardBuilder extends Builder {
  constructor(fragmentContainer, targetElement, styler, props) {
    super(fragmentContainer, targetElement, styler);
    this.props = props;
    this.previousMentionType = null;
  }

  static PROPERTY_TYPE = {
    name: 'Type',
    value: function(ment) { return ment.type; }
  };

  doBuild() {
    const table = document.createElement('TABLE');
    table.appendChild(this.buildThead());
    table.appendChild(this.buildTbody());
    return table;
  }

  buildThead() {
    const thead = Builder.createElement('THEAD');
    const tr = Builder.createElement('TR', null, thead);
    const th = Builder.createElement('TH', '&nbsp;', tr);
    th.id = 'mention-card-head';
    th.setAttribute('colspan', '2');
    return thead;
  }

  buildTbody() {
    const tbody = Builder.createElement('TBODY');
    for (let p of this.props) {
      const tr = Builder.createElement('TR', null, tbody);
      tr.id = p.name;
      const tdName = Builder.createElement('TD', p.name, tr);
      tdName.classList.add('prop-name');
      const tdValue = Builder.createElement('TD', null, tr);
      tdValue.classList.add('prop-value')
    }
    return tbody;
  }

  clearHighlight() {
    super.clearHighlight();
    const th = this.buildRoot.querySelector('th#mention-card-head');
    th.innerHTML = '∅';
    if (this.previousMentionType) {
      th.classList.remove(this.previousMentionType);
      this.previousMentionType = null;
    }
    for (let td of this.buildRoot.querySelectorAll('td.prop-value')) {
      td.classList.innerHTML = '';
    }
  }

  highlight(mention) {
    super.highlight(mention);
    const th = this.buildRoot.querySelector('th#mention-card-head');
    th.innerHTML = mention.isEmpty ? '∅' : mention.form;
    if (this.previousMentionType) {
      th.classList.remove(this.previousMentionType);
    }
    th.classList.add(mention.type);
    this.previousMentionType = mention.type;
    for (let p of this.props) {
      const td = this.buildRoot.querySelector(`tr#${p.name} td.prop-value`);
      td.innerHTML = p.value(mention);
    }
  }
}

class TableBuilder extends Builder {
  constructor(fragmentContainer, targetElement, styler, columns) {
    super(fragmentContainer, targetElement, styler);
    this.columns = columns;
  }

  static COLUMN_INDEX = {
    name: 'N',
    content: function(ment, index) { return `${index + 1}`; },
    type: Number
  };

  static COLUMN_TYPE = {
    name: 'Type',
    content: function(ment, index) { return ment.type; },
    type: String
  };

  static COLUMN_FORM = {
    name: 'Form',
    content: function(ment, index) { return ment.isEmpty ? '∅' : ment.form; },
    type: String
  }

  doBuild() {
    const table = document.createElement('TABLE');
    table.appendChild(this.buildThead());
    table.appendChild(this.buildTbody());
    return table;
  }

  static _getCurrentSort(th) {
    const cl = th.classList;
    if (cl.contains('sort-asc')) {
      return 'sort-asc';
    }
    if (cl.contains('sort-desc')) {
      return 'sort-desc';
    }
    return 'sort-none';
  }

  static _rowCompare(th) {
    return function(a, b) {
      const vA = th.colType(a.querySelector(`td:nth-of-type(${th.colIdx+1})`).textContent);
      const vB = th.colType(b.querySelector(`td:nth-of-type(${th.colIdx+1})`).textContent);
      if (vA < vB) { return -1; }
      if (vA > vB) { return 1; }
      return 0;
    }
  }

  static _sortByColumn(event) {
    const th = event.target;
    const thead = th.parentElement.parentElement;
    const table = thead.parentElement;
    const tbody = table.querySelector('tbody');
    const rows = [...tbody.querySelectorAll('tr')];
    const currentSort = TableBuilder._getCurrentSort(th);
    thead.querySelectorAll('tr th').forEach(th => { th.classList.remove('sort-desc', 'sort-asc'); th.classList.add('sort-none'); });
    th.classList.remove('sort-none');
    switch (currentSort) {
      case 'sort-none': {
        rows.sort(TableBuilder._rowCompare(th));
        th.classList.add('sort-asc');
        break;
      }
      case 'sort-asc': {
        rows.reverse();
        th.classList.add('sort-desc');
        break;
      }
      case 'sort-desc': {
        rows.reverse();
        th.classList.add('sort-asc');
        break;
      }
    }
    tbody.replaceChildren(...rows);
  }

  buildThead() {
    const thead = document.createElement('THEAD');
    const tr = Builder.createElement('TR', null, thead);
    this.columns.forEach(function (col, i) {
      const th = Builder.createElement('TH', col.name, tr);
      th.colIdx = i;
      th.colType = col.type;
      th.onclick = TableBuilder._sortByColumn;
      th.classList.add(col.name, 'sort-none');
    });
    return thead;
  }

  buildTbody() {
    const tbody = document.createElement('TBODY');
    this.fragmentContainer.mentions.forEach((ment, index) => tbody.appendChild(this.buildRow(ment, index)))
    return tbody;
  }

  buildRow(ment, index) {
    const tr = document.createElement('TR');
    ment.stamp(tr);
    const last = this.columns.length - 1;
    this.columns.forEach(function (col, i) {
      const cell = col.content(ment, index);
      const td = Builder.createElement('TD', cell, tr);
      td.classList.add(col.name);
      if (i === 0) {
        td.classList.add('first-column')
      }
      if (i === last) {
        td.classList.add('last-column');
      }
    });
    return tr;
  }

  clearHighlight() {
    super.clearHighlight();
    this.buildRoot.querySelectorAll(`.mention.highlight`).forEach(e => e.classList.remove('highlight'));
  }

  highlight(mention) {
    super.highlight(mention);
    this.buildRoot.querySelectorAll(`.mention-${mention.id}`).forEach(e => e.classList.add('highlight'));
  }
}

class FragmentBuilder extends Builder {
  constructor(fragmentContainer, targetElement, styler) {
    super(fragmentContainer, targetElement, styler);
  }

  reset() {
    const buildRoot = document.createElement('DIV');
    this.data = [{fragment: null, element: buildRoot}];
    this.lastOffset = 0;
    return buildRoot;
  }

  getPoints() {
    const result = [];
    for (let frag of this.fragmentContainer.fragments) {
      if (frag.isEmpty) {
        result.push(new EmptyPoint(frag));
      }
      else {
        result.push(new StartPoint(frag));
        result.push(new EndPoint(frag));
      }
    }
    return result.sort(Point.compare);
  }

  doBuild() {
    const buildRoot = this.reset();
    const points = this.getPoints();
    this.fragmentContainer.updateLevels();
    points.forEach(p => p.build(this));
    this.pushString();
    return buildRoot;
  }

  pushString(offset) {
    this._append(this.fragmentContainer.text.substring(this.lastOffset, offset));
    this.lastOffset = offset;
  }

  _append(node) {
    const par = this.peek().element;
    par.append(node);
  }

  push(frag) {
    const elt = frag.createElement();
    if (frag.isMention) {
      elt.classList.add(`mention-level-${frag.level}`);
    }
    this._append(elt);
    this.data.push({fragment: frag, element: elt});
    return elt;
  }

  pop() {
    return this.data.pop();
  }

  peek() {
    return this.data[this.data.length - 1];
  }

  clearHighlight() {
    super.clearHighlight();
    this.buildRoot.querySelectorAll(`.mention.highlight`).forEach(e => e.classList.remove('highlight'));
  }

  highlight(mention) {
    super.highlight(mention);
    this.buildRoot.querySelectorAll(`.mention-${mention.id}`).forEach(e => e.classList.add('highlight'));
  }
}

class Point {
  constructor(pointOrder, fragment) {
    this.pointOrder = pointOrder;
    this.fragment = fragment;
  }

  get offset() {
    return this.getOffset();
  }

  getOffset() {
    throw new Error('Not Implemented');
  }

  get otherOffset() {
    return this.getOtherOffset();
  }

  getOtherOffset() {
    throw new Error('Not Implemented');
  }

  correctCompare(cmp) {
    throw new Error('Not Implemented');
  }

  build(builder) {
    builder.pushString(this.offset);
    this.buildAux(builder);
  }

  buildAux(builder) {
    throw new Error('Not Implemented');
  }

  static compare(a, b) {
    if (a.offset !== b.offset) {
      return a.offset - b.offset;
    }
    if (a.pointOrder !== b.pointOrder) {
      return a.pointOrder - b.pointOrder;
    }
    if (a.otherOffset !== b.otherOffset) {
      return b.otherOffset - a.otherOffset;
    }
    if (a.fragment.fragmentOrder !== b.fragment.fragmentOrder) {
      return a.correctCompare(a.fragment.fragmentOrder - b.fragment.fragmentOrder);
    }
    if (a.fragment.typeOrder !== b.fragment.typeOrder) {
      return a.correctCompare(a.fragment.typeOrder - b.fragment.typeOrder);
    }
    if (a.fragment.order !== b.fragment.order) {
      return a.correctCompare(a.fragment.order - b.fragment.order);
    }
    return 0;
  }
}

class StartPoint extends Point {
  constructor(fragment) {
    super(2, fragment);
    console.assert(!fragment.isEmpty, 'should be EmptyPoint');
  }

  getOffset() {
    return this.fragment.start;
  }

  getOtherOffset() {
    return this.fragment.end;
  }

  correctCompare(cmp) {
    return cmp;
  }

  buildAux(builder) {
    const elt = builder.push(this.fragment);
    if (this.fragment.isMention) {
      elt.classList.add('first-slice', 'not-empty-mention');
    }
  }
}

class EndPoint extends Point {
  constructor(fragment) {
    super(0, fragment);
    console.assert(!fragment.isEmpty, 'should be EmptyPoint');
  }

  getOffset() {
    return this.fragment.end;
  }

  getOtherOffset() {
    return this.fragment.start;
  }

  correctCompare(cmp) {
    return -cmp;
  }

  buildAux(builder) {
    const unclosed = [];
    while (true) {
      const item = builder.pop();
      if (item.fragment === this.fragment) {
        if (item.fragment.isMention) {
          const elt = item.element;
          elt.classList.add('last-slice');
          if (unclosed.length) {
            elt.classList.add('slicing-end');
          }
        }
        break;
      }
      unclosed.unshift(item);
    }
    for (const item of unclosed) {
      const newElt = builder.push(item.fragment);
      if (item.fragment.isMention) {
        item.element.classList.add('not-last-slice');
        newElt.classList.add('not-first-slice');
      }
    }
  }
}

class EmptyPoint extends Point {
  constructor(fragment) {
    super(1, fragment);
    console.assert(fragment.isEmpty, 'should not be EmptyPoint');
  }

  getOffset() {
    return this.fragment.start;
  }

  getOtherOffset() {
    return this.fragment.end;
  }

  correctCompare(cmp) {
    return -cmp;
  }

  buildAux(builder) {
    const elt = builder.push(this.fragment);
    builder.pop();
    if (this.fragment.isMention) {
      elt.classList.add('first-slice', 'last-slice', 'empty-mention');
    }
  }
}

class MentionStyler {
  static STYLESHEET_TITLE = 'mention-styles';

  constructor(options={}) {
    this.options = options;
    this.rawCSS = options.rawCSS || [];
  }

  createStyle() {
    const style = document.createElement('style');
    style.type = 'text/css';
    style.title = MentionStyler.STYLESHEET_TITLE;
    style.id = MentionStyler.STYLESHEET_TITLE;
    style.innerHTML = this.buildStyleSheetRules().join('\n');
    return style;
  }

  static coerceArray(o) {
    if (typeof o === 'string') {
      return [o];
    }
    return o;
  }

  buildStyleSheetRules() {
    return MentionStyler.coerceArray(this.rawCSS);
  }
}

class ColorMentionStyler extends MentionStyler {
  constructor(fragmentContainer, options={}) {
    super(options);
    this.fragmentContainer = fragmentContainer;
    this.mentionTypeColors = ColorMentionStyler._buildMentionTypeColors(fragmentContainer, options);
  }

  static _buildMentionTypeColors(fragmentContainer, options) {
    if (options.mentionTypeColors) {
      return Object.assign({'_default': 'lightgrey'}, options.mentionTypeColors);
    }
    if (options.mentionTypeColorFactory) {
      const mtcf = options.mentionTypeColorFactory;
      switch (mtcf.method || 'spread') {
        case 'random': {
          return ColorMentionStyler._defaultColors(ColorMentionStyler._randomPalette, fragmentContainer, mtcf.saturation, mtcf.lightness);
        }
        case 'spread': {
          return ColorMentionStyler._defaultColors(ColorMentionStyler._spreadPalette, fragmentContainer, mtcf.startHue, mtcf.saturation, mtcf.lightness);
        }
      }
    }
    return {'_default': 'lightgrey'};
  }

  static _defaultColors(paletteFactory, fragmentContainer, ...args) {
    const types = fragmentContainer.mentionTypes;
    const colors = paletteFactory(types.length, ...args);
    return Object.fromEntries(types.map((t, i) => [t, colors[i]]));
  }

  static _spreadPalette(n, startHue=0, saturation=50, lightness=80) {
    const step = Math.round(360 / n);
    return Array.from(
      { length: n },
      (_, i) => {
        const hue = startHue + i * step;
        return ColorMentionStyler.hsl(hue, saturation, lightness);
    });
  }

  static _randomPalette(n, saturation=50, lightness=80) {
    return Array.from(
      { length: n },
      (_, i) => {
        const hue = Math.floor(Math.random() * 360);
        return ColorMentionStyler.hsl(hue, saturation, lightness);
    });
  }

  static hsl(h, s, l) {
    return `hsl(${h % 360},${s}%,${l}%)`;
  }

  buildStyleSheetRules() {
    const rules = super.buildStyleSheetRules();
    for (const mType of this.fragmentContainer.mentionTypes) {
      const color = this.mentionTypeColors[mType] || this.mentionTypeColors['_default'];
      rules.push(this.buildMentionTypeRule(mType, color));
    }
    return rules;
  }

  buildMentionTypeRule(mType, color) {
    throw new Error('Not Implemented');
  }
}

class FragmentHighlightColorMentionStyler extends ColorMentionStyler {
  constructor(fragmentContainer, options={}) {
    super(fragmentContainer, options);
    this.nestPadding = options.nestPadding || 4;
    this.borderWidth = options.borderWidth || 2;
    this.cornerRadius = options.cornerRadius || 6;
    this.leftBorderColor = options.leftBorderColor || 'darkgrey';
    this.rightBorderColor = options.rightBorderColor || 'lightgrey';
    this.emptyMention = options.emptyMention || '∅';
    this.highlightBorderColor = options.highlightBorderColor || 'red';
  }

  buildStyleSheetRules() {
    const result = super.buildStyleSheetRules();
    const maxLevel = this.fragmentContainer.maxLevel;
    result.push(`div { line-height: ${150+maxLevel*70}%}`)
    for (let lvl = 0; lvl <= maxLevel; ++lvl) {
      result.push(`.mention-level-${lvl} { padding-top: ${lvl*this.nestPadding}px; padding-bottom: ${lvl*this.nestPadding}px; }`);
    }
    result.push(`span.mention { cursor: default; border-top: solid ${this.borderWidth}px ${this.leftBorderColor}; border-bottom: solid ${this.borderWidth}px ${this.rightBorderColor}; }`);
    result.push(`span.mention.first-slice { border-left: solid ${this.borderWidth}px ${this.leftBorderColor}; border-top-left-radius: ${this.cornerRadius}px; }`)
    result.push(`span.mention.last-slice { border-right: solid ${this.borderWidth}px ${this.rightBorderColor}; border-bottom-right-radius: ${this.cornerRadius}px; }`)
    result.push('span.mention.not-last-slice { padding-right: 0px; }');
    result.push('span.mention.not-first-slice { padding-left: 0px; }');
    result.push('span.mention.slicing-end { padding-right: 0px; border-right: none; }');
    result.push(`span.mention.empty-mention::before { content: "${this.emptyMention}"; }`);
    result.push(`span.mention.highlight { border-color: ${this.highlightBorderColor}; filter: drop-shadow(0px 0px ${this.borderWidth}px ${this.highlightBorderColor}); }`);
    return result;
  }

  buildMentionTypeRule(mType, color) {
    return `span.mention.${mType} { background-color: ${color}; }`
  }
}

class MentionCardStyler extends ColorMentionStyler {
  constructor(fragmentContainer, options={}) {
    super(fragmentContainer, options);
  }

  buildStyleSheetRules() {
    const result = super.buildStyleSheetRules();
    result.push('table { border-spacing: 0px; width: 100%; }');
    result.push('thead th { border-bottom: solid 5px white; font-style: italic; text-align: left; }');
    result.push('td.prop-name { font-weight: bold; font-family: sans; width: 25%; border-bottom: solid 1px lightgrey; }');
    result.push('td.prop-value { width: 75%; border-bottom: solid 1px lightgrey; text-align: right; }')
    return result;
  }

  buildMentionTypeRule(mType, color) {
    return `th.${mType} { background-color: ${color}; }\ntr.mention.${mType} td { border-color: ${color}; }`;
  }
}

class TableHighlightColorMentionStyler extends ColorMentionStyler {
  constructor(fragmentContainer, options={}) {
    super(fragmentContainer, options);
    this.highlightBorderColor = options.highlightBorderColor || 'red';
  }

  buildStyleSheetRules() {
    const result = super.buildStyleSheetRules();
    result.push('table { border-spacing: 0px; width: 100%; }');
    result.push('th { cursor: default; min-width: 3ch; }');
    result.push('th { text-align: left; font-family: sans; }');
    result.push('th::before { display: inline-block; width: 1ch; margin-right: 3px; }')
    result.push('th.sort-none::before { content: "•"; color: lightgrey; }')
    result.push('th.sort-asc::before { content: "⌃"; color: black; }')
    result.push('th.sort-desc::before { content: "⌄"; color: black; }')
    result.push('td { border-top-style: solid; border-bottom-style: solid; border-width: 2px; padding: 2px; }');
    result.push(`tr.mention.highlight td { border-top-style: solid; border-bottom-style: solid; border-width: 2px; border-color: ${this.highlightBorderColor}; }`);
    return result;
  }

  buildMentionTypeRule(mType, color) {
    return `tr.mention.${mType} { background-color: ${color}; }\ntr.mention.${mType} td { border-color: ${color}; }`;
  }
}
