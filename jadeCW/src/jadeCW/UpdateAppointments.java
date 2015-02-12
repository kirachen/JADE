package jadeCW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class UpdateAppointments extends CyclicBehaviour {

	private HospitalAgent hospital;
	private List<Tuple> swappingPatients = new ArrayList<Tuple>();

	@Override
	public void action() {
		hospital = (HospitalAgent) myAgent;
		ACLMessage update = hospital.receive();
		if (update != null) {
			if (update.getPerformative() == ACLMessage.INFORM) {
				AID patient = update.getSender();
				AID swappingWithPatient;
				try {
					swappingWithPatient = (AID) update.getContentObject();
					Tuple swap = new Tuple(patient, swappingWithPatient);
					if (confirmedByFirstPatient(swap)) {
						hospital.swap(patient, swappingWithPatient);
						broadCastUpdate();
					} else {
						swappingPatients.add(swap);
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void broadCastUpdate() {
		ArrayList<AID> patientList = (ArrayList<AID>) Arrays.asList(hospital.getPatientList());
		ACLMessage broadcastMsg = new ACLMessage(ACLMessage.INFORM_REF);
		try {
			broadcastMsg.setContentObject(patientList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<AID> patients = hospital.getPatients();
		for (AID patient : patients) {
			broadcastMsg.addReceiver(patient);
		}
		hospital.send(broadcastMsg);
	}

	private boolean confirmedByFirstPatient(Tuple swap) {
		if (!swappingPatients.isEmpty()) {
			for (Tuple t : swappingPatients) {
				if (t.equals(swap)) {
					// Once confirmed, remove it from the list, so the list
					// always only have pending swapping request from one side
					swappingPatients.remove(t);
					return true;
				}
			}
		}
		return false;
	}

	private class Tuple {

		private AID patient1;
		private AID patient2;

		private Tuple(AID patient1, AID patient2) {
			this.patient1 = patient1;
			this.patient2 = patient2;
		}

		private AID getPatient1() {
			return patient1;
		}

		private AID getPatient2() {
			return patient2;
		}

		// Comparing p2 with patient1 and p1 with patient2 to confirm the
		// swapping has been requested by the other agent too. Not comparing p1
		// with patient1, p2 with patient 2 to avoid double confirming by the
		// same patient
		private boolean equals(Tuple t) {
			AID p1 = t.getPatient1();
			AID p2 = t.getPatient2();
			return p2.equals(patient1) && p1.equals(patient2);
		}
	}

}
