package jadeCW;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class RequestAppointment extends Behaviour {
	private String serviceType = "allocate-appointments";
	private AID[] hospitalAgents;
	private PatientAgent patient = (PatientAgent) myAgent;

	@Override
	public void action() {
		if (serviceExists() && !patient.hasAppointment()) {
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
				patient.setServiceProvider(msg.getSender());
				System.out.println("patient" + patient.getName() + " has been allocated with an appointment " + appointment);
			}
		} else {
			block();
		}
	}

	private void requestAppointment() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		for (AID id : hospitalAgents) {
			msg.addReceiver(id);
		}
		patient.send(msg);
		System.out.println("patient" + patient.getName() + " requests for available appointment");
	}

	private boolean serviceExists() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(serviceType);
		template.addServices(sd);
		try {
			DFAgentDescription[] results = DFService.search(patient, template);
			if (results.length > 0) {
				hospitalAgents = new AID[results.length];
				for (int i = 0; i < results.length; i++) {
					hospitalAgents[i] = results[i].getName();
					System.out.println(serviceType + " service found:");
					System.out.println("- Service \"" + sd.getName());
				}
				return true;
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		return false;
	}
}
