package jadeCW;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RespondToProposal2 extends CyclicBehaviour {

	HospitalAgent hospital;

	@Override
	public void action() {
		hospital = (HospitalAgent) myAgent;
		ACLMessage proposal = hospital.receive();
		if (proposal != null) {
			if (proposal.getPerformative() == ACLMessage.PROPOSE) {
				AID patient = proposal.getSender();
				String proposedAppointment = proposal.getContent();
				ACLMessage reply = proposal.createReply();
				if (hospital.getAvailableAppointments().contains(
						proposedAppointment)) {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					System.out.println(hospital.getLocalName()
							+ " accepting proposal for changing appointment "
							+ proposedAppointment + " for "
							+ patient.getLocalName());
					hospital.allocateAppointment(proposedAppointment, patient);
				} else {
					AID owner = hospital.getOwner(proposedAppointment);
					try {
						reply.setContentObject(owner);
					} catch (IOException e) {
						e.printStackTrace();
					}
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					System.out.println(hospital.getLocalName()
							+ " rejecting proposal for changing appointment "
							+ proposedAppointment + " with owner " + owner.getLocalName() + " for"
							+ patient.getLocalName());
				}
				hospital.send(reply);
			}
		} else {
			block();
		}

	}

}
