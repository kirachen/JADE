package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FindAppointmentOwner extends Behaviour {

	private PatientAgent patient;
	private AID serviceProvider;
	private boolean informed = false;

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		serviceProvider = patient.getServiceProvider();
		String preferedAppointment = patient.getMorePreferedAppointment();
		if (!preferedAppointment.equals("null")) {
			if (!informed) {
				requestAppointment(preferedAppointment);
				receiveResponse(preferedAppointment);
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

	private void receiveResponse(String preferedAppointment) {
		ACLMessage msg = patient.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.INFORM && msg.getSender().equals(patient.getServiceProvider())) {
				informed = true;
				String owner = msg.getContent();
				//patient.setOwnerofPreferedApmnt(preferedAppointment);
				if (owner.equals("owner:null")) {
					System.out.println("owner of appointment "
							+ preferedAppointment + " is not known");
				} else if (owner.equals("appointment:null")) {
					System.out.println("appointment " + preferedAppointment
							+ " is unknown");
				} else {
					System.out.println(owner
							+ " is the owner of appointment "
							+ preferedAppointment);
				}
			}
		} else {
			block();
		}
	}

}
