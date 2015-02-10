package jadeCW;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RespondToProposal2 extends CyclicBehaviour {

	HospitalAgent hospital;

	public RespondToProposal2() {
		hospital = (HospitalAgent) myAgent;
	}

	@Override
	public void action() {
		ACLMessage proposal = hospital.receive();
		AID patient = proposal.getSender();
		if (proposal != null) {
			if (proposal.getPerformative() == ACLMessage.PROPOSE) {
				String proposedAppointment = proposal.getContent();
				ACLMessage reply = proposal.createReply();
				if (hospital.getAvailableAppointments().contains(
						proposedAppointment)) {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					hospital.allocateAppointment(proposedAppointment, patient);
				} else {
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				}
			}
		}

	}

}
