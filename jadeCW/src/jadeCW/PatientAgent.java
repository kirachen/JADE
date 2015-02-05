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

	private AID serviceProvider;
	private List<List<String>> preferenceList;
	private String allocatedAppointment = "null";

	protected void setup() {
		final String serviceType = "allocate-appointments";

		Object[] arguments = getArguments();
		if (arguments != null && arguments.length > 0) {
			processPrefs(arguments);
			printPrefs();
		}
		

		subscribeService(serviceType);
		addBehaviour(new RequestAppointment());
		//addBehaviour(new FindAppointmentOwner());
	}
	
	protected void takeDown() {
		System.out.println("patient" + this.getName() + ":Appointment" + allocatedAppointment);
	}
	
	protected AID getServiceProvider() {
		return serviceProvider;
	}
	
	protected boolean hasAppointment() {
		return !allocatedAppointment.equals("null");
	}
	
	protected void allocateAppointment(String appointment) {
		allocatedAppointment = appointment;
	}
	
	protected List<List<String>> getPreferenceList() {
		return preferenceList;
	}

	private void printPrefs() {
		System.out.println("patient " + this.getName() + " prefers: ");
		for (List<String> level : preferenceList) {
			for (String pref : level) {
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
		addBehaviour(new SubscriptionInitiator(this,
				DFService.createSubscriptionMessage(this, getDefaultDF(),
						template, sc)) {
			protected void handleInform(ACLMessage inform) {
				System.out.println("Agent " + getLocalName()
						+ ": Notification received from HospitalAgent");
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
									serviceProvider = provider;
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
		preferenceList = new ArrayList<List<String>>();
		int prefLevel = 0;
		List<String> prefs = new ArrayList<String>();
		for (int i = 0; i < arguments.length; i++) {
			String arg = (String) arguments[i];
			Character pref = arg.charAt(0);
			if (Character.isDigit(pref)) {
				prefs.add(pref.toString());
			} else if (pref.equals('-')) {
				preferenceList.add(prefLevel, prefs);
				prefs = new ArrayList<String>();
				prefLevel++;
			}
		}
	}

	protected String getMorePreferedAppointment() {
		if (allocatedAppointment.equals("null")) {
			return String.valueOf(preferenceList.get(0).get(0));
		} else {
			Integer appointment = Integer.valueOf(allocatedAppointment);
			int prefLevel = -1;
			for (int i=0; i< preferenceList.size(); i++) {
				if (preferenceList.get(i).contains(appointment)) {
					prefLevel = i;
				}
			}
			if (prefLevel > 0) {
				return preferenceList.get(prefLevel-1).get(0);
			}
		}
		return "null";
	}
}
