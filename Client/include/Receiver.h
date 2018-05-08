#ifndef ECHOCLIENT_RECEIVERM_H_
#define ECHOCLIENT_RECEIVERM_H_

using namespace std;

namespace msg{
enum message{
	ASKTXT,
	ASKCHOICES,
	SYSMSG,
	GAMEMSG,
	USRMSG,
	NOTEXISTS
};
}

class Receiver {
public:
	Receiver();
	ConnectionHandler* handler;
	void opertaor();
	Receiver(ConnectionHandler *hand);
	virtual ~Receiver();
};

#endif /* ECHOCLIENT_RECEIVERM_H_ */
