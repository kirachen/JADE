package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class FindAppointmentOwner extends Behaviour {

	private PatientAgent patient;
	private AID serviceProvider;
	private boolean informed = false;
	
	public FindAppointmentOwner() {
		patient = (PatientAgent) myAgent;
		serviceProvider = patient.getServiceProvider();
	}

	@Override
	public void action() {
		String preferedAppointment = patient.getMorePreferedAppointment();
		if (preferedAppointment != null) {
			if (!informed) {
				requestAppointment(preferedAppointment);
				try {
					receiveResponse(preferedAppointment);
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean done() {
		return informed;
	}

	private void requestAppointment(String preferedAppointment) {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.setContent(preferedAppointment);
		msg.addReceiver(serviceProvider);
		patient.send(msg);
		System.out.println(patient.getLocalName()
				+ " requests for prefered appointment " + preferedAppointment);
	}

	private void receiveResponse(String preferedAppointment)
			throws UnreadableException {
		ACLMessage msg = patient.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.INFORM
					&& msg.getSender().equals(patient.getServiceProvider())) {
				informed = true;
				AID owner = (AID) msg.getContentObject();
				if (!msg.getContent().isEmpty()) {
					System.out.println(msg.getContent());
				} else {
					patient.setPreferedAppointmentOwner(owner);
					System.out.println(owner.getLocalName()
							+ " is the owner of appointment "
							+ preferedAppointment);
				}
			}
		} else {
			block();
		}
	}

}
