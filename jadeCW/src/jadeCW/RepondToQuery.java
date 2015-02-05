package jadeCW;

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
				processQuery(msg);
			}
		} else {
			block();
		}
	}

	private void processQuery(ACLMessage msg) {
		ACLMessage reply = msg.createReply();
		reply.setPerformative(ACLMessage.INFORM);
		String appointment = msg.getContent();
		String owner = hospital.getOwner(appointment);
		if (owner.equals("null")) {
			reply.setContent("owner:null");
		} else if (owner.equals("appointment:null")) {
			reply.setContent("appointment:null");
		} else {
			reply.setContent(owner);
		}
		hospital.send(reply);
	}

}
