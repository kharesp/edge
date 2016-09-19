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
		if (args.length < 3){
			System.out.println("Arguments: domainId topicName filePath expected");
			return;
		}

		int domainId = DOMAIN_ID;
		domainId = Integer.valueOf(args[0]).intValue();

		String topicName=TOPIC;
		topicName=args[1];


		String filePath=READINGS_PATH;
		filePath=args[2];
		
		publish(domainId,topicName,filePath);
		
	}
	

	public static void publish(int domainId,String topicName,String filePath) {

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
			DomainParticipantQos pQos= new DomainParticipantQos();
			participant.get_qos(pQos);
			System.out.println("rtps_host_id:"+pQos.wire_protocol.rtps_host_id);
					
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

			topic = participant.create_topic(topicName, 
					typeName,
					DomainParticipant.TOPIC_QOS_DEFAULT,
					null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (topic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			DataWriterQos wQos= new DataWriterQos();
			participant.get_default_datawriter_qos(wQos);
			System.out.println("Default dw qos:"+wQos.history.kind);
			System.out.println("Default dw qos:"+wQos.reliability.kind);

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

			System.out.println("Publisher will wait for discovery of all subscribers");
			PublicationMatchedStatus pubStatus = new PublicationMatchedStatus();
			while(true){
				writer.get_publication_matched_status(pubStatus);
				if(pubStatus.current_count==1)
					break;
				System.out.println("Publisher is waiting for discovery. Current subscriber count:"+pubStatus.current_count);
				Thread.sleep(5000);
			}
			System.out.format("Publisher discovered %d subscribers and will start sending data\n",pubStatus.current_count);

			Observable<String> lines = ParseData.readFile(filePath);
			lines.map(l -> new Reading(l.split(","))).subscribe(r -> {
				final_writer.write(r, instance_handle);
			});
			while(true){
				Thread.sleep(10000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {

			if (participant != null) {
				participant.delete_contained_entities();
				DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
			}
		}
	}
		
}
