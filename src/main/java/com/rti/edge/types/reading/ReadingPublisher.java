package com.rti.edge.types.reading;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.*;
import com.rti.dds.topic.*;
import com.rti.edge.parse.ParseData;
import rx.Observable;

public class ReadingPublisher {

	private static final int DOMAIN_ID=0;
	private static final String TOPIC="Readings";
	private static final String READINGS_PATH="/vagrant/DEBS2014/sample50.csv";

	public static void main(String[] args) {
		// --- Get domain ID --- //
		int domainId = DOMAIN_ID;
		if (args.length >= 1) {
			domainId = Integer.valueOf(args[0]).intValue();
		}
		String filePath=READINGS_PATH;
		if (args.length >= 2){
			filePath=args[1];
		}

		publish(domainId,filePath);
	}
	

	public static void publish(int domainId,String filePath) {

		DomainParticipant participant = null;
		Publisher publisher = null;
		Topic topic = null;
		ReadingDataWriter writer = null;

		try {
			participant = DomainParticipantFactory.TheParticipantFactory.
					create_participant(domainId,
							DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
							null /* listener */,
							StatusKind.STATUS_MASK_NONE);
			if (participant == null) {
				System.err.println("create_participant error\n");
				return;
			}

			publisher = participant.create_publisher(
					DomainParticipant.PUBLISHER_QOS_DEFAULT,
					null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (publisher == null) {
				System.err.println("create_publisher error\n");
				return;
			}

			String typeName = ReadingTypeSupport.get_type_name();
			ReadingTypeSupport.register_type(participant, typeName);

			topic = participant.create_topic(TOPIC, 
					typeName,
					DomainParticipant.TOPIC_QOS_DEFAULT,
					null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (topic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			writer = (ReadingDataWriter) publisher.create_datawriter(
					topic, 
					Publisher.DATAWRITER_QOS_DEFAULT,
					null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (writer == null) {
				System.err.println("create_datawriter error\n");
				return;
			}
			final ReadingDataWriter final_writer= writer;

			InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;

			Observable<String> lines = ParseData.readFile(filePath);
			lines.map(l -> new Reading(l.split(","))).subscribe(r -> {
				final_writer.write(r, instance_handle);
			});
			Thread.sleep(100*1000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {

			if (participant != null) {
				participant.delete_contained_entities();
				DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
			}
		}
	}
		
}