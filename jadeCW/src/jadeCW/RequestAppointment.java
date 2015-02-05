package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class RequestAppointment extends Behaviour {
	private PatientAgent patient;

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		if (patient.getServiceProvider() != null && !patient.hasAppointment()) {
			requestAppointment();
			receiveResponse();
		}
	}
	
	@Override
	public boolean done() {
		return patient.hasAppointment();
	}

	private void receiveResponse() {
		ACLMessage msg = patient.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.AGREE) {
				String appointment = msg.getContent();
				patient.allocateAppointment(appointment);
				System.out.println("patient " + patient.getName() + " has been allocated with an appointment " + appointment);
			}
		} else {
			block();
		}
	}

	private void requestAppointment() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(patient.getServiceProvider());
		patient.send(msg);
		System.out.println("patient " + patient.getName() + " requests for available appointment");
	}
}
