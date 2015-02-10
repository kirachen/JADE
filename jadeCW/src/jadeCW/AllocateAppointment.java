package jadeCW;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class AllocateAppointment extends CyclicBehaviour {

	HospitalAgent hospital;

	@Override
	public void action() {
		hospital = (HospitalAgent) myAgent;
		ACLMessage msg = hospital.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.REQUEST) {
				processAppointmentRequest(msg);
			}
		} else {
			block();
		}
	}

	private void processAppointmentRequest(ACLMessage msg) {
		ACLMessage reply = msg.createReply();
		if (hospital.hasAvailableAppointment()) {
			List<String> availableAppointments = hospital
					.getAvailableAppointments();
			String availableAppointment = availableAppointments.get(0);
			reply.setPerformative(ACLMessage.AGREE);
			reply.setContent(availableAppointment);
			AID patient = msg.getSender();
			hospital.allocateAppointment(availableAppointment, patient);
			System.out.println(hospital.getLocalName()
					+ " has allocated appointment " + availableAppointment
					+ " to " + patient);
		} else {
			reply.setPerformative(ACLMessage.REFUSE);
			System.out.println(hospital.getLocalName()
					+ " does not have any available appointment");
		}
		myAgent.send(reply);
	}

}
