package jadeCW;

import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class RespondToUpdate extends CyclicBehaviour {

	private PatientAgent patient;
	private ProposeSwap proposeSwap;

	public RespondToUpdate(ProposeSwap proposeSwap) {
		this.proposeSwap = proposeSwap;
	}

	@Override
	public void action() {
		patient = (PatientAgent) myAgent;
		ACLMessage update = myAgent.receive();
		if (update != null) {
			if (update.getPerformative() == ACLMessage.INFORM_REF) {
				try {
					List<AID> appointments = (List<AID>) update.getContentObject();
					String appointment = patient.getMorePreferedAppointment();
					int index = Integer.valueOf(appointment);
					if (ownerChanged(appointments, index)) {
						AID newOwner = appointments.get(index-1);
						patient.setPreferedAppointmentOwner(newOwner);
						proposeSwap.action();
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		} else {
			block();
		}
	}

	private boolean ownerChanged(List<AID> appointments, int index) {
		return !appointments.get(index-1).equals(patient.getPreferedAppointmentOwner());
	}

}
