package jadeCW;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class UpdateAppointments extends CyclicBehaviour {

	private HospitalAgent hospital;
	
	public UpdateAppointments() {
		hospital = (HospitalAgent) myAgent;
	}
	@Override
	public void action() {
		ACLMessage update = hospital.receive();
		if (update != null) {
			if (update.getPerformative() == ACLMessage.INFORM) {
				AID patient = update.getSender();
				AID swappingWithPatient;
				try {
					swappingWithPatient = (AID) update.getContentObject();
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
