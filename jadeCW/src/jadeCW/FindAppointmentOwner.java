package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class FindAppointmentOwner extends Behaviour {

	PatientAgent patient = (PatientAgent) myAgent;
	AID serviceProvider = patient.getServiceProvider();

	@Override
	public void action() {
		String preferedAppointment = patient.getMorePreferedAppointment();
		if (!preferedAppointment.equals("null")) {
			requestAppointment(preferedAppointment);
			receiveResponse(preferedAppointment);
		}
	}

	@Override
	public boolean done() {
		
		return false;
	}

	private void requestAppointment(String preferedAppointment) {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.setContent(preferedAppointment);
		msg.addReceiver(serviceProvider);
		patient.send(msg);
		System.out.println("patient" + patient.getName()
				+ " requests for prefered appointment " + preferedAppointment);
	}

	private void receiveResponse(String preferedAppointment) {
		ACLMessage msg = patient.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.INFORM) {
				String owner = msg.getContent();
				if (owner.equals("owner:null")) {
					System.out.println("owner of appointment "
							+ preferedAppointment + " is not known");
				} else if (owner.equals("appointment:null")) {
					System.out.println("appointment " + preferedAppointment
							+ " is unknown");
				} else {
					System.out.println("patient" + owner
							+ " is the owner of appointment "
							+ preferedAppointment);
				}
			}
		} else {
			block();
		}
	}

}
