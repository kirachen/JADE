package jadeCW;

import java.util.List;

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
			String patientName = msg.getSender()
					.getName();
			hospital.allocateAppointment(availableAppointment, patientName);
			System.out.println("hospital " + hospital.getName()
					+ " has allocated appointment " + availableAppointment
					+ " to " + patientName);
		} else {
			reply.setPerformative(ACLMessage.REFUSE);
			System.out.println("hospital " + hospital.getName()
					+ " does not have any available appointment");
		}
		myAgent.send(reply);
	}

}