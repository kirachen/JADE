package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class FindAppointmentOwner extends Behaviour {

	private PatientAgent patient;
	private AID serviceProvider;
	private boolean informed = false;

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		serviceProvider = patient.getServiceProvider();
		if (serviceProvider != null && patient.hasAppointment()
				&& patient.getMorePreferedAppointment() != null) {
			if (!informed) {
				findOwner(patient.getMorePreferedAppointment());
			}
			ACLMessage msg = patient.blockingReceive();
			if (msg != null) {
				try {
					receiveResponse(patient.getMorePreferedAppointment(), msg);
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

	private void findOwner(String preferedAppointment) {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		msg.setContent(preferedAppointment);
		msg.addReceiver(serviceProvider);
		System.out.println(patient.getPatientState()
				+ " queries for the owner of prefered appointment "
				+ preferedAppointment);
		patient.send(msg);
	}

	private void receiveResponse(String preferedAppointment, ACLMessage msg)
			throws UnreadableException {
		if (msg.getPerformative() == ACLMessage.INFORM
				&& msg.getSender().equals(patient.getServiceProvider())) {
			AID owner = (AID) msg.getContentObject();
			if (msg.getContent().equals("appointment:unknown")) {
				System.out.println(msg.getContent());
			} else {
				patient.setPreferedAppointmentOwner(owner);
				System.out
						.println(patient.getPatientState() + " received that " + owner.getLocalName()
								+ " is the owner of appointment "
								+ preferedAppointment);
			}
			informed = true;
		}
	}

}
