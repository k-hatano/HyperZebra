# Makefile
HyperZebra: HyperZebra.java
	javac -encoding utf8 -d classes *.java
	jar cvfm HyperZebra.jar mani.mf -C classes/ .
clean:
	-rm *.jar
	-rm classes/*.class
