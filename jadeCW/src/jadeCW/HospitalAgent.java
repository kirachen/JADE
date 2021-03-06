package jadeCW;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HospitalAgent extends Agent {

	private AID[] patientList;

	protected void setup() {
		int numberOfAppointments = 0;
		String serviceName = "hospital agent";
		String serviceType = "allocate-appointments";

		// Read the number of appointments
		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			String appointments = (String) arguments[0];
			numberOfAppointments = Integer.valueOf(appointments);
		}
		patientList = new AID[numberOfAppointments];
		// Register the service
		System.out.println("Agent " + getName()
				+ " registering service of type " + serviceType);
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(serviceType);
			// Agents that want to use this service need to "know" the
			// weather-forecast-ontology
			sd.addOntologies("allocate-appointment-ontology");
			sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
			sd.addProperties(new Property("country", "UK"));
			dfd.addServices(sd);

			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new AllocateAppointment());
		addBehaviour(new RepondToQuery());
		addBehaviour(new RespondToProposal2());
		addBehaviour(new UpdateAppointments());
	}

	protected void takeDown() {
		for (int i = 0; i < patientList.length; i++) {
			AID patient = patientList[i];
			if (patient != null) {
				System.out.println(this.getLocalName() + ":Appointment"
						+ (i + 1) + ":" + patient.getLocalName());
			} else {
				System.out.println(this.getLocalName() + ":Appointment"
						+ (i + 1) + ":null");
			}
		}
	}

	protected synchronized AID[] getPatientList() {
		return patientList;
	}
	
	protected synchronized List<AID> getPatients() {
		List<AID> patients = new ArrayList<AID>();
		for (AID id : patientList) {
			if (id != null) {
				patients.add(id);
			}
		}
		return patients;
	}
	
	protected synchronized boolean hasAvailableAppointment() {
		for (AID appointment : patientList) {
			if (appointment == null) {
				return true;
			}
		}
		return false;
	}

	protected synchronized List<String> getAvailableAppointments() {
		List<String> appointments = new ArrayList<String>();
		for (int i = 0; i < patientList.length; i++) {
			if (patientList[i] == null) {
				appointments.add(String.valueOf(i + 1));
			}
		}
		return appointments;
	}

	protected synchronized void allocateAppointment(String availableAppointment, AID patient) {
		Integer index = Integer.valueOf(availableAppointment);
		patientList[index - 1] = patient;
	}

	protected synchronized AID getOwner(String appointment) {
		Integer index = Integer.valueOf(appointment);
		if (index <= patientList.length || index > 0) {
			if (patientList[index - 1] != null) {
				return patientList[index - 1];
			} else {
				return this.getAID();
			}
		}
		return null;
	}

	protected synchronized void swap(AID patient, AID swappingWithPatient) {
		List<AID> list = Arrays.asList(patientList);
		int appointment1 = list.indexOf(patient);
		int appointment2 = list.indexOf(swappingWithPatient);
		patientList[appointment1] = swappingWithPatient;
		patientList[appointment2] = patient;
	}
}
