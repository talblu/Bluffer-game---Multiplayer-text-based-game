package bgu.spl.server;

/* *
* A protocol that describes the behavior of the server .
*/
public interface AsyncServerProtocol <T > extends ServerProtocol <T > {
/* *
* Processes a message .
*
* @param msg
 the message to process
* @param callback an instance of ProtocolCallback unique to the
connection from which msg originated .
*/
void processMessage ( T msg , ProtocolCallback <T > callback ) ;
/* *
* Detetmine whether the given message is the termination message .
*
* @param msg the message to examine
* @return true if the message is the termination message , false
otherwise
*/
boolean isEnd ( T msg ) ;
/* *
* Is the protocol in a closing state ?.
* When a protocol is in a closing state , it ’s handler should write
out all pending data ,
* and close the connection .
*
* @return true if the protocol is in closing state .
*/
boolean shouldClose () ;

/* *
* Indicate to the protocol that the client disconnected .*/
void connectionTerminated () ;
}
