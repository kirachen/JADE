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
				receiveResponse();
			}
		}
	}

	@Override
	public boolean done() {
		return requested;
	}

	private void receiveResponse() {
		ACLMessage msg = patient.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.AGREE && msg.getSender().equals(patient.getServiceProvider())) {
				String appointment = msg.getContent();
				patient.allocateAppointment(appointment);
				System.out.println(patient.getLocalName()
						+ " has been allocated with an appointment "
						+ appointment);
				waitingForResponse = false;
				requested = true;
			}
		} else {
			block();
		}
	}

	private void requestAppointment() {
		waitingForResponse = true;
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(patient.getServiceProvider());
		patient.send(msg);
		System.out.println(patient.getLocalName()
				+ " requests for available appointment");
	}
}
