package jadeCW;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RepondToQuery extends CyclicBehaviour{

	private HospitalAgent hospital;

	@Override
	public void action() {
		hospital = (HospitalAgent) myAgent;
		ACLMessage msg = hospital.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.QUERY_IF) {
				try {
					processQuery(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			block();
		}
	}

	private void processQuery(ACLMessage msg) throws IOException {
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		String appointment = msg.getContent();
		AID owner = hospital.getOwner(appointment);
		if (owner == null) {
			reply.setContent("appointment:unknown");
		} else {
			reply.setContentObject(owner);
		}
		hospital.send(reply);
	}

}
