package jadeCW;

import jade.core.AID;
import jade.core.Agent;
import jade.core.LifeCycle;
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

	private List<List<Integer>> preferenceList;
	private Integer allocatedAppointment;

	protected void setup() {
		final String serviceType = "allocate-appointments";

		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			processPrefs(arguments);
		}
		printPrefs();

		subscribeService(serviceType);
		addBehaviour(new RequestAppointment());
	}
	
	protected boolean hasAppointment() {
		return allocatedAppointment != 0;
	}
	
	protected void allocateAppointment(Integer appointment) {
		allocatedAppointment = appointment;
	}
	
	protected List<List<Integer>> getPreferenceList() {
		return preferenceList;
	}

	private void printPrefs() {
		System.out.println("patient prefers: ");
		for (List<Integer> level : preferenceList) {
			for (Integer pref : level) {
				System.out.println("Appointment" + pref);
			}
			if (preferenceList.indexOf(level) != (preferenceList.size() - 1)) {
				System.out.println("Then");
			}
		}
	}

	private void subscribeService(final String serviceType) {
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
									System.out.println(serviceType
											+ " service found:");
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

	private void processPrefs(Object[] arguments) {
		preferenceList = new ArrayList<List<Integer>>();
		int prefLevel = 0;
		List<Integer> prefs = new ArrayList<Integer>();
		for (int i = 0; i < arguments.length; i++) {
			String arg = (String) arguments[i];
			Character pref = arg.charAt(0);
			if (Character.isDigit(pref)) {
				System.out.println(pref + " is digit");
				System.out.println(Character.getNumericValue(pref));
				prefs.add(Character.getNumericValue(pref));
			} else if (pref.equals('-')) {
				preferenceList.add(prefLevel, prefs);
				prefs = new ArrayList<Integer>();
				prefLevel++;
			}
		}
	}
}
