import alvisnlp
import sys
import stanza
from concurrent.futures import process


class PretokenizedStub:
    @staticmethod
    def get_doc(sec):
        return list(list(w.form for w in sec.words(s)) for s in sec.sentences())

    @staticmethod
    def get_sentences(stanza_doc, sec):
        return zip(stanza_doc.sentences, sec.sentences())

    @staticmethod
    def get_token_map(sec, stanza_sent, anlp_sent):
        return dict((stanza_tok.id, anlp_tok) for stanza_tok, anlp_tok in zip(stanza_sent.tokens, sec.words(anlp_sent)))


class RawStub:
    @staticmethod
    def get_doc(sec):
        return sec.contents

    @staticmethod
    def convert_sentence(sec, stanza_sent):
        return sec.create_annotation('sentences', stanza_sent.tokens[0].start_char, stanza_sent.tokens[-1].end_char)

    @staticmethod
    def get_sentences(stanza_doc, sec):
        for stanza_sent in stanza_doc.sentences:
            yield stanza_sent, RawStub.convert_sentence(sec, stanza_sent)

    @staticmethod
    def get_token_map(sec, stanza_sent, anlp_sent):
        token_map = {}
        for tok in stanza_sent.tokens:
            a = sec.create_annotation('words', tok.start_char, tok.end_char)
            token_map[tok.id] = a
        return token_map


class StanzaApp:
    def __init__(self):
        self.corpus = alvisnlp.Corpus.parse_json(sys.stdin)
        self.pretokenized = self.b('pretokenized')
        self.lang = self.corpus.params['lang']
        self.parse = self.b('parse')
        self.ner = self.b('ner')
        self.constituency = self.b('constituency')

    def b(self, param):
        s = self.corpus.params[param]
        return s == 'yes' or s == 'y' or s == 'true'

    def get_processors(self):
        processors = ['tokenize', 'mwt', 'pos', 'lemma']
        if self.parse:
            processors.append('depparse')
        if self.constituency:
            processors.append('constituency')
        if self.ner:
            processors.append('ner')
        return processors

    def get_stanza_pipeline(self):
        processors = self.get_processors()
        return stanza.Pipeline(lang=self.lang, processors=','.join(processors), tokenize_pretokenized=self.pretokenized)

    def get_stub(self):
        if self.pretokenized:
            return PretokenizedStub
        return RawStub

    @staticmethod
    def convert_token_info(stanza_sent, token_map):
        for tok in stanza_sent.tokens:
            a = token_map[tok.id]
            if len(tok.words) > 1:
                sys.stderr.write('token has multiple words: %s\n' % ', '.join(w.text for w in tok.words))
            w = tok.words[0]
            a.features['lemma'] = w.lemma
            a.features['pos'] = w.xpos
            a.features['upos'] = w.upos
            a.features['morph'] = w.feats
            a.features['stanza-id'] = str(tok.id)

    @staticmethod
    def convert_dependencies(sec, anlp_sent, stanza_sent, token_map):
        for head, label, tail in stanza_sent.dependencies:
            ta = token_map[tail.parent.id]
            if head.parent is None:
                sec.create_tuple('dependencies', args=dict(dependent=ta, sentence=anlp_sent), features=dict(label=label))
            else:
                ha = token_map[head.parent.id]
                sec.create_tuple('dependencies', args=dict(head=ha, dependent=ta, sentence=anlp_sent), features=dict(label=label))

    @staticmethod
    def _convert_tree_recursive(sec, const):
        if not const.is_preterminal():
            prets = list(const.yield_preterminals())
            start = min(pret.anlp_token.start for pret in prets)
            end = max(pret.anlp_token.end for pret in prets)
            sec.create_annotation('constituents', start, end, features=dict(label=const.label))
            for child in const.children:
                StanzaApp._convert_tree_recursive(sec, child)

    @staticmethod
    def convert_constituencies(sec, anlp_sent, stanza_sent, token_map):
        for pret, stanza_word in zip(stanza_sent.constituency.yield_preterminals(), stanza_sent.words):
            anlp_token = token_map[stanza_word.parent.id]
            pret.anlp_token = anlp_token
        StanzaApp._convert_tree_recursive(sec, stanza_sent.constituency)
            
    @staticmethod
    def convert_entities(sec, stanza_sent, token_map):
        for ent in stanza_sent.ents:
            start = token_map[ent.tokens[0].id].start
            end = token_map[ent.tokens[-1].id].end
            sec.create_annotation('entities', start, end, features=dict(type=ent.type))

    def run(self):
        nlp = self.get_stanza_pipeline()
        stub = self.get_stub()
        for doc in self.corpus.documents:
            for sec in doc.sections:
                stanza_doc = nlp(stub.get_doc(sec))
                for stanza_sent, anlp_sent in stub.get_sentences(stanza_doc, sec):
                    token_map = stub.get_token_map(sec, stanza_sent, anlp_sent)
                    StanzaApp.convert_token_info(stanza_sent, token_map)
                    StanzaApp.convert_dependencies(sec, anlp_sent, stanza_sent, token_map)
                    StanzaApp.convert_constituencies(sec, anlp_sent, stanza_sent, token_map)
                    StanzaApp.convert_entities(sec, stanza_sent, token_map)
        self.corpus.write_events_json(sys.stdout)


StanzaApp().run()
