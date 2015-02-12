package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;

public class ProposeSwap extends Behaviour {

	private PatientAgent patient;
	private boolean done = false;

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		AID preferedAppointmentOwner = patient.getPreferedAppointmentOwner();
		if (patient.getServiceProvider() != null
				&& preferedAppointmentOwner != null) {
			if (!done) {
				ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
				proposal.setContent(patient.getAllocatedAppointment());
				proposal.addReceiver(preferedAppointmentOwner);
				System.out.println(patient.getPatientState()
						+ " proposes a swap for prefered appointment "
						+ patient.getMorePreferedAppointment() + " with "
						+ preferedAppointmentOwner.getLocalName());
				patient.send(proposal);
			}
			ACLMessage response = patient.blockingReceive();
			if (response != null) {
				if (response.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					String newAppointment = response.getContent();
					patient.allocateAppointment(newAppointment);
					if (!preferedAppointmentOwner.equals(patient
							.getServiceProvider())) {
						try {
							informHospital(preferedAppointmentOwner);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else if (response.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
					AID owner = null;
					try {
						owner = (AID) response.getContentObject();
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
					patient.setPreferedAppointmentOwner(owner);
				}
				done = true;
			}
		}
	}

	@Override
	public boolean done() {
		return done;
	}

	private void informHospital(AID preferedAppointmentOwner)
			throws IOException {
		ACLMessage info = new ACLMessage(ACLMessage.INFORM);
		info.setContentObject(preferedAppointmentOwner);
		info.addReceiver(patient.getServiceProvider());
		System.out.println(patient.getPatientState()
				+ " informing hospital for swapping");
		patient.send(info);
	}
}
