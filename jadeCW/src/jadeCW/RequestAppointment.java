package jadeCW;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class RequestAppointment extends Behaviour {
	private PatientAgent patient;
	private boolean waitingForResponse = false;
	private boolean requested = false;

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		if (patient.getServiceProvider() != null && !patient.hasAppointment()) {
			if (!waitingForResponse) {
				requestAppointment();
			}
			ACLMessage msg = patient.receive();
			if (msg != null) {
				receiveResponse(msg);
			}
		}
	}

	@Override
	public boolean done() {
		return requested;
	}

	private void receiveResponse(ACLMessage msg) {
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.AGREE && msg.getSender().equals(patient.getServiceProvider())) {
				String appointment = msg.getContent();
				patient.allocateAppointment(appointment);
				System.out.println(patient.getPatientState()
						+ " has been allocated with appointment "
						+ appointment);
				waitingForResponse = false;
				requested = true;
			}
		}
	}

	private void requestAppointment() {
		waitingForResponse = true;
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(patient.getServiceProvider());
		System.out.println(patient.getPatientState()
				+ " requests for available appointment");
		patient.send(msg);
	}
}
