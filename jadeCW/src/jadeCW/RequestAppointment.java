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
	private PatientAgent agent = (PatientAgent) myAgent;

	@Override
	public void action() {
		if (serviceExist() && !agent.hasAppointment()) {
			requestAppointment();
			receiveResponse();
		}
	}

	private void receiveResponse() {
		ACLMessage msg = agent.receive();
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.AGREE) {
				String content = msg.getContent();
				Integer appointment = Integer.valueOf(content);
				agent.allocateAppointment(appointment);
			}
		} else {
			block();
		}
	}

	private void requestAppointment() {
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
		for (AID id : hospitalAgents) {
			msg.addReceiver(id);
		}
		agent.send(msg);
	}

	private boolean serviceExist() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(serviceType);
		template.addServices(sd);
		try {
			DFAgentDescription[] results = DFService.search(agent, template);
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

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return false;
	}

}
