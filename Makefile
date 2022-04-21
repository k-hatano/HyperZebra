$(eval SRCS := $(shell find src -name "*.java" | tr '\r' ' '))

HyperZebra: $(SRCS)
	javac -encoding utf8 -d classes $^
	jar cvfm HyperZebra.jar mani.mf -C classes/ .
