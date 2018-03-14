#FROM maven:3.2-jdk-7-onbuild
FROM ubuntu:14.04 
MAINTAINER Mouhamadou Ba <mouhamadou.ba@inra.fr>
#
#
# general tools
RUN apt-get -yqq update && apt-get -yqq install \
    maven \
    git \
    expect \
    wget \
    xmlstarlet \
    zip \
    # for the python-based tools
    python \
    python-numpy \
    make \
    ruby \
    g++ \
    gcc \
    libboost-all-dev \
    flex && \
    # java
    apt-get install -y  software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists*

ENV java_version oracle-java8

## pulling and installing alvisnlp
RUN git clone -b docker-sources https://github.com/Bibliome/alvisnlp.git /alvisnlp && \
    cd /alvisnlp && \
    # compiling and installing alvisnlp
    mvn clean install && \
    # install alvisnlp
    ./install.sh . && \
    # remove maven dependencies
    rm -rf ~/.m2 && \
    # remove extras
    rm -rf doc* alvisnlp-test test-alvisnlp.sh && \
    # create the external soft dir
    mkdir psoft

# external softs workdir
WORKDIR /alvisnlp/psoft
RUN cp /alvisnlp/share/default-param-values.xml.template /alvisnlp/share/default-param-values.xml && \
    ## installing biolg 
    #wget http://staff.cs.utu.fi/~spyysalo/biolg/biolg-1.1.12.tar.gz && \
    #mkdir biolg-1.1.12 && tar xvzf biolg-1.1.12.tar.gz -C biolg-1.1.12/ && \
    #rm biolg-1.1.12.tar.gz && \
    #cd biolg-1.1.12/pcre-5.0 && ./configure && cd ../.. && \
    #cd biolg-1.1.12/expat-2.0.0 && ./configure && cd ../.. && \
    #cd biolg-1.1.12 && make && cd ../ && \
    ## intalling ccgparser
    wget http://www.cl.cam.ac.uk/%7Esc609/resources/candc-downloads/candc-linux-1.00.tgz && \
    tar xvf candc-linux-1.00.tgz && \
    rm candc-linux-1.00.tgz && \
    # not required cd candc-1.00/ && make && cd /alvisnlp/psoft && \
    ## installing enjuparser /!\ download link does work
    ## installing enjuparser 2 /!\ download link does work
    ## install geniatagger
    wget http://www.nactem.ac.uk/tsujii/GENIA/tagger/geniatagger-3.0.2.tar.gz && \
    tar xvf geniatagger-3.0.2.tar.gz && \
    rm geniatagger-3.0.2.tar.gz && \
    cd geniatagger-3.0.2/ && make && cd /alvisnlp/psoft && \
    ## installing SPECIES
    wget http://download.jensenlab.org/species_tagger.tar.gz && \
    tar xvf species_tagger.tar.gz && \
    rm  species_tagger.tar.gz && \
    cd species_tagger && make && cd /alvisnlp/psoft && \
    ## installing StanfordNER 2014-06-16*
    wget https://nlp.stanford.edu/software/stanford-ner-2014-06-16.zip && \
    unzip stanford-ner-2014-06-16.zip && \
    rm stanford-ner-2014-06-16.zip && \
    # not required cd stanford-ner-2014-06-16/ && /alvisnlp/psoft ../ && \
    ## installing tees
    wget https://github.com/jbjorne/TEES/tarball/master && \
    tar xvf master && \
    rm -rf master && \
    mv *-TEES-*  tees && \
    cd tees/  && \
    # creating the expect file
    wget https://raw.githubusercontent.com/openminted/alvis-docker/master/script.exp && \
    chmod +x script.exp && \
    ./script.exp && \
    export TEES_SETTINGS=$pwd/tees_local_settings.py && \
    cd /alvisnlp/psoft && \
    ## installing treeTagger
    wget http://www.cis.uni-muenchen.de/%7Eschmid/tools/TreeTagger/data/tree-tagger-linux-3.2.1.tar.gz && \
    mkdir treetagger/ && tar xvf tree-tagger-linux-3.2.1.tar.gz -C treetagger && \
    rm tree-tagger-linux-3.2.1.tar.gz && \
    cd treetagger/ && cd /alvisnlp/psoft  && \
    mkdir treetagger/lib && \ 
    cd treetagger/lib && \
    wget http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/english-par-linux-3.2-utf8.bin.gz && \
    gunzip -N english-par-linux-3.2-utf8.bin.gz && \
    mv english-utf8.par english.par && \
    cd /alvisnlp/psoft && \
    ## installing wapiti
    wget https://wapiti.limsi.fr/wapiti-1.5.0.tar.gz && \
    tar xvf wapiti-1.5.0.tar.gz && \
    rm wapiti-1.5.0.tar.gz && \
    cd  wapiti-1.5.0 && make && make install && cd /alvisnlp/psoft && \
    ## installing yatea /!\ seems to require Q&A during installation
    wget http://search.cpan.org/CPAN/authors/id/T/TH/THHAMON/Lingua-YaTeA-0.622.tar.gz && \
    tar xvf Lingua-YaTeA-0.622.tar.gz && \
    rm  Lingua-YaTeA-0.622.tar.gz && \
    cd Lingua-YaTeA-0.622 && \
    cpan App::cpanminus && \
    cpanm Lingua::YaTeA && \
    cd /alvisnlp/psoft && \
    ## params values setting
    cat /alvisnlp/share/default-param-values.xml | \
    ## ccg parser params values setting
    xmlstarlet ed -u "//module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGParser']/executable" -v /alvisnlp/psoft/candc-1.00/bin/parser | \
    xmlstarlet ed -d "//module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGParser']/parserModel" | \
    xmlstarlet ed -d "//module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGParser']/superModel" | \
    ## ccg postagger params values setting
    xmlstarlet ed -u "//module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGPosTagger']/executable" -v /alvisnlp/psoft/candc-1.00/bin/pos | \
    xmlstarlet ed -d "//module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ccg.CCGPosTagger']/model" | \
    ## geniatagger params values setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.geniatagger.GeniaTagger']/geniaDir" -v /alvisnlp/psoft/geniatagger-3.0.2  | \
    ## species tagger param setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.Species']/speciesDir" -v /alvisnlp/psoft/species_tagger/ | \
    ## stanford param values setting
    xmlstarlet ed -d "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.stanford.StanfordNER']/classifierFile" | \
    ## tees classify params values setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.TEESClassify']/teesHome" -v /alvisnlp/psoft/tees/ | \
    ## tees train params values setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tees.TEESTrain']/teesHome" -v /alvisnlp/psoft/tees/ | \
    ## wapiti label params values setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiLabel']/wapitiExecutable" -v /usr/local/bin/wapiti | \
    ## wapiti train params values setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.wapiti.WapitiTrain']/wapitiExecutable" -v /usr/local/bin/wapiti | \
    ## treetagger
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger.TreeTagger']/treeTaggerExecutable" -v /alvisnlp/psoft/treetagger/bin/tree-tagger  | \
    xmlstarlet ed -d "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger.TreeTagger']/parFile" | \
    ## yatea params values setting
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractor']/yateaExecutable" -v /usr/local/bin/yatea  | \
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractor']/rcFile" -v res://yatea.rc | \
    #xmlstarlet ed -d /default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractor']/rcFile" | \
    xmlstarlet ed -d "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractor']/configDir" | \
    xmlstarlet ed -d "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.yatea.YateaExtractor']/localeDir" | \
    ## tomap params 
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap.TomapTrain']/yateaExecutable" -v /usr/local/bin/yatea | \
    xmlstarlet ed -u "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap.TomapTrain']/rcFile" -v res://yatea.rc | \
    xmlstarlet ed -d "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap.TomapTrain']/configDir" | \
    xmlstarlet ed -d "/default-param-values/module[@class='fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.tomap.TomapTrain']/localeDir" | \
    tee /alvisnlp/share/default-param-values.xml
#
WORKDIR /alvisnlp
#
ENV PATH /alvisnlp/bin:$PATH
#
# ENTRYPOINT ["/alvisnlp/bin/alvisnlp"]
#
CMD ["alvisnlp"]
