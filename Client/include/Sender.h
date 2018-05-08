#ifndef ECHOCLIENT_SENDERM_H_
#define ECHOCLIENT_SENDERM_H_
#include "ConnectionHandler.h"

class Sender {
private:
	ConnectionHandler* handler;

public:
	Sender();
	 void opertaor();
	 Sender(ConnectionHandler *hand);
	virtual ~Sender();
};

#endif /* ECHOCLIENT_SENDERM_H_ */
