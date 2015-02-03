package jadeCW;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class HospitalAgent extends Agent {
	protected void setup() {
		int numberOfAppointments = 0;
		String serviceType = "allocate-appointments";

		// Read the number of appointments to register as an argument
		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			numberOfAppointments = (Integer) arguments[0];
		}
		for (int i = 0; i < numberOfAppointments; i++) {
			// Register the service
			System.out.println("Agent " + getLocalName()
					+ " registering service of type " + serviceType);
			try {
				DFAgentDescription dfd = new DFAgentDescription();
				dfd.setName(getAID());
				ServiceDescription sd = new ServiceDescription();
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
	}
}
