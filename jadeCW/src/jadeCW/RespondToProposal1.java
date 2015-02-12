package jadeCW;

import java.io.IOException;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class RespondToProposal1 extends CyclicBehaviour {

	private PatientAgent patient;

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		ACLMessage proposal = patient.receive();
		if (proposal != null) {
			if (proposal.getPerformative() == ACLMessage.PROPOSE) {
				String proposedAppointment = proposal.getContent();
				ACLMessage reply = proposal.createReply();
				if (patient.isMorePrefered(proposedAppointment)) {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					reply.setContent(patient.getAllocatedAppointment());
					System.out.println(patient.getPatientState()
							+ " accepting proposal for swapping with "
							+ proposal.getSender().getLocalName()
							+ " for appointment " + proposedAppointment);
				} else {
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					System.out.println(patient.getLocalName()
							+ " rejecting proposal for swapping with "
							+ proposal.getSender().getLocalName()
							+ " for appointment " + proposedAppointment);
				}
				patient.send(reply);
				patient.allocateAppointment(proposedAppointment);
				try {
					informHospital(patient.getServiceProvider());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			block();
		}
	}

	private void informHospital(AID preferedAppointmentOwner)
			throws IOException {
		ACLMessage info = new ACLMessage(ACLMessage.INFORM);
		info.setContentObject(preferedAppointmentOwner);
		info.addReceiver(patient.getServiceProvider());
		System.out.println(patient.getPatientState() + " informing hospital for swapping");
		patient.send(info);
	}

}
