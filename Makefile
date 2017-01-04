JFLAGS =
JCC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JCC) $(JFLAGS) $*.java

CLASSES = \
	AuctionItem.java \
	AuctionSystemInterface.java \
	AuctionSystem.java \
	Server.java \
	Client.java \
	Account.java \
	AccountInterface.java

default: run

compileclasses: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

reset:
	./reset.sh

run: compileclasses
	rmiregistry &
	x-terminal-emulator -e java Server
	sleep 5
	x-terminal-emulator -e java Client
