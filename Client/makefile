CFLAGS:=-c -Wall -Weffc++ -g
LDFLAGS:=-lboost_system -lboost_locale -lboost_thread

all: bin/encoder.o gameClient
	g++ -o bin/echoExample bin/encoder.o bin/ConnectionHandler.o bin/EchoClient.o $(LDFLAGS) 

gameClient: bin/ConnectionHandler.o bin/EchoClient.o
	
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/EchoClient.o: src/EchoClient.cpp
	g++ $(CFLAGS) -o bin/EchoClient.o src/EchoClient.cpp
	
bin/encoder.o: src/encoder.cpp
	g++ $(CFLAGS) -o bin/encoder.o src/encoder.cpp
	
.PHONY: clean
clean:
	rm -f bin/*