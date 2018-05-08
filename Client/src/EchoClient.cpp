#include <stdlib.h>
#include <boost/locale.hpp>
#include "../include/ConnectionHandler.h"
#include "../include/utf8.h"
#include "../include/encoder.h"
#include <boost/thread.hpp>
#include <iostream>

/**
 * This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
 */
void  Sender(ConnectionHandler & handler, bool &isClosed){
	while (1) {
		if(isClosed){
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			break;
		}
		const short bufsize = 1024;
		char buf[bufsize];
		std::cin.getline(buf, bufsize);
		std::string line(buf);

		if ( !handler.sendLine(line)) {
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			break;
		}
	}
}

void  Receiver(ConnectionHandler &handler, bool &isClosed){
	int len; // help us not to get down a line
	while(1){
		std::string answer;

		if (!handler.getLine(answer)) {
			std::cout << "Disconnected. Exiting...\n" << std::endl;
			isClosed=true;
			break;
		}
		len=answer.length();
		answer.resize(len-1);
		if(answer == "SYSMSG QUIT ACCEPTED"){
			isClosed=true;
			break;
		}
		std::cout << answer << std::endl;

	}
}

int main (int argc, char *argv[]) {
	if (argc < 3) {
		std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
		return -1;
	}
	std::string host = argv[1];
	short port = atoi(argv[2]);
	bool isClosed;
	ConnectionHandler connectionHandler(host, port);
	if (!connectionHandler.connect()) {
		std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
		return 1;
	}

	boost::thread sender(Sender,boost::ref(connectionHandler),boost::ref(isClosed));
	boost::thread receiver(Receiver,boost::ref(connectionHandler),boost::ref(isClosed));

	receiver.join();

	std::cout << "Bye Bye"<<std::endl;
	return 0;
}
