package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

public class ProposeSwap extends Behaviour {

	private PatientAgent patient;
	private boolean done = false;

	public ProposeSwap() {
		patient = (PatientAgent) myAgent;
	}

	@Override
	public void action() {
		ACLMessage response = patient.receive();
		AID preferedAppointmentOwner = patient.getPreferedAppointmentOwner();
		if (preferedAppointmentOwner != null) {
			ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
			proposal.setContent(patient.getAllocatedAppointment());
			proposal.addReceiver(preferedAppointmentOwner);
			System.out.println(patient.getLocalName()
					+ " propose a swap for prefered appointment with "
					+ preferedAppointmentOwner.getLocalName());
			patient.send(proposal);
		}
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
			}
			done = true;
		}
	}

	@Override
	public boolean done() {
		return done;
	}
	
	private void informHospital(AID preferedAppointmentOwner) throws IOException {
		ACLMessage info = new ACLMessage(ACLMessage.INFORM);
		info.setContentObject(preferedAppointmentOwner);
		info.addReceiver(patient.getServiceProvider());
		System.out.println(patient.getLocalName() + " informing hospital for swapping");
		patient.send(info);
	}
}
