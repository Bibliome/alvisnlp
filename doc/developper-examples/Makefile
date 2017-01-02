JAVAC=javac
JAR=jar

LIBS=../../lib/util.jar:../../lib/alvisnlp.jar
JAVASRC=$(shell find src -name '*.java')

hello.jar: build
	$(JAR) cf $@ -C $< .

build: gen-src $(JAVASRC)
	mkdir $@
	$(JAVAC) -cp $(LIBS) -d $@ -processorpath $(LIBS) -s $< -AmoduleFactoryName=alvisnlp.hello.HelloFactory $(JAVASRC)
	touch $@

gen-src:
	mkdir $@

clean:
	$(RM) -r build gen-src
	$(RM) hello.jar
