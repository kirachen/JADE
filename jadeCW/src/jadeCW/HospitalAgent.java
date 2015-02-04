package jadeCW;

import java.util.ArrayList;
import java.util.List;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class HospitalAgent extends Agent {
	
	private List<String> patientList;
	
	protected void setup() {
		patientList = new ArrayList<String>();
		int numberOfAppointments = 0;
		String serviceName = "hospital agent";
		String serviceType = "allocate-appointments";

		// Read the number of appointments to register as an argument
		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			String appointments = (String) arguments[0];
			numberOfAppointments = Integer.valueOf(appointments);
		}
		initPatientList(numberOfAppointments);
		// Register the service
		System.out.println("Agent " + getLocalName()
				+ " registering service of type " + serviceType);
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(serviceType);
			// Agents that want to use this service need to "know" the
			// allocate-appointments-ontology
			sd.addOntologies(serviceType + "-ontology");
			// Agents that want to use this service need to "speak" the
			// FIPA-SL language
			sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
			dfd.addServices(sd);

			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private void initPatientList(int numberOfAppointments) {
		for(int i=0; i< numberOfAppointments; i++) {
			patientList.add(i, "null");
			System.out.println("hospital1:" + "Appointment" + (i+1) + ":null");
		}
	}
}
