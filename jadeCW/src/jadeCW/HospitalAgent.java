package jadeCW;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.List;

public class HospitalAgent extends Agent {
	
	private List<String> patientList;
	
	protected void setup() {
		patientList = new ArrayList<String>();
		int numberOfAppointments = 0;
		String serviceName = "hospital agent";
		String serviceType = "allocate-appointments";

		// Read the number of appointments
		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			String appointments = (String) arguments[0];
			numberOfAppointments = Integer.valueOf(appointments);
		}
		initPatientList(numberOfAppointments);
		// Register the service
		System.out.println("Agent " + getName()
				+ " registering service of type " + serviceType);
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(serviceType);
			sd.addLanguages("English");
			dfd.addServices(sd);

			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new AllocateAppointment());
	}
	
	protected void takeDown() {
		for(int i=0; i< patientList.size(); i++) {
			System.out.println("hospital1:" + "Appointment" + (i+1) + ":" + patientList.get(i));
		}
	}
	
	protected boolean hasAvailableAppointment() {
		for (String appointment : patientList) {
			if (!appointment.equals("null")) {
				return true;
			}
		}
		return false;
	}
	
	protected List<String> getAvailableAppointments() {
		List<String> appointments = new ArrayList<String>();
		for (int i=0; i<patientList.size(); i++) {
			if (!patientList.get(i).equals("null")) {
				appointments.add(String.valueOf(i+1));
			}
		}
		return appointments;
	}
	
	protected void allocateAppointment(String availableAppointment, String patient) {
		Integer index = Integer.valueOf(availableAppointment);
		patientList.set(index, patient);
	}

	private void initPatientList(int numberOfAppointments) {
		for(int i=0; i< numberOfAppointments; i++) {
			patientList.add(i, "null");
		}
	}
}
