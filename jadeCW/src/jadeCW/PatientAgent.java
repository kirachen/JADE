package jadeCW;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.util.leap.Iterator;

import java.util.ArrayList;
import java.util.List;

public class PatientAgent extends Agent {

	private ArrayList<List<Integer>> preferenceList;

	protected void setup() {
		Object[] prefs;
		final String serviceType = "allocate-appointments";

		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			processPrefs(arguments);
		}

		// Build the description used as template for the subscription
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription templateSd = new ServiceDescription();
		templateSd.setType(serviceType);
		template.addServices(templateSd);

		SearchConstraints sc = new SearchConstraints();
		// We want to receive 10 results at most
		sc.setMaxResults(new Long(10));

		addBehaviour(new SubscriptionInitiator(this,
				DFService.createSubscriptionMessage(this, getDefaultDF(),
						template, sc)) {
			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent " + getLocalName()
						+ ": Notification received from DF");
				try {
					DFAgentDescription[] results = DFService
							.decodeNotification(inform.getContent());
					if (results.length > 0) {
						for (int i = 0; i < results.length; ++i) {
							DFAgentDescription dfd = results[i];
							AID provider = dfd.getName();
							// The same agent may provide several services; we
							// are only interested
							// in the appointment allocation one
							Iterator it = dfd.getAllServices();
							while (it.hasNext()) {
								ServiceDescription sd = (ServiceDescription) it
										.next();
								if (sd.getType().equals(serviceType)) {
									System.out
											.println(serviceType + " service found:");
									System.out.println("- Service \""
											+ sd.getName()
											+ "\" provided by agent "
											+ provider.getName());
								}
							}
						}
					}
					System.out.println();
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});

	}
	
	protected ArrayList<List<Integer>> getPreferences() {
		return preferenceList;
	}

	private void processPrefs(Object[] arguments) {
		int prefLevel = 0;
		List<Integer> prefs = new ArrayList<Integer>();
		for (int i = 0; i < arguments.length; i++) {
			Character pref = (Character) arguments[i];
			if (Character.isDigit(pref)) {
				prefs.add(Integer.valueOf(pref));
			} else if (pref.equals('-')) {
				preferenceList.add(prefLevel, prefs);
				prefs.clear();
				prefLevel++;
			}
		}

	}
}
