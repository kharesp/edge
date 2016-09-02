package com.rti.edge.edgent;

import org.apache.edgent.function.Consumer;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TypeSupportImpl;

public class DDSPublisherConnector<T> implements Consumer<T> {
	private static final long serialVersionUID = 1L;
	private String topicName;
	private Class<T> typeClass;
	private TypeSupportImpl typeSupport;
	private DomainParticipant participant;
	private Publisher publisher;
	private Topic topic;
	private DataWriter writer;
	private InstanceHandle_t instance_handle;
	
	
	public DDSPublisherConnector(String topicName, Class<T> typeClass, TypeSupportImpl typeSupport)
	{
		this.topicName= topicName;
		this.typeClass= typeClass;
		this.typeSupport=typeSupport;
		try{
			participant=DefaultParticipant.instance();
		}catch(Exception e){
			System.out.println("Failed to initialize default participant");
		}
		initialize();
	}

	private void initialize(){
		publisher = participant.create_publisher(
				DomainParticipant.PUBLISHER_QOS_DEFAULT,
				null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (publisher == null) {
			System.err.println("create_publisher error\n");
			return;
		}
		try {
			DefaultParticipant.registerType(typeClass.getSimpleName(),typeSupport);
		} catch (Exception e) {
			e.printStackTrace();
		}
		topic = participant.create_topic(topicName, typeClass.getSimpleName(), DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (topic == null) {
			System.err.println("create_topic error\n");
			return;
		}

		writer = publisher.create_datawriter(
				topic, 
				Publisher.DATAWRITER_QOS_DEFAULT,
				null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (writer == null) {
			System.err.println("create_datawriter error\n");
			return;
		}
		instance_handle= InstanceHandle_t.HANDLE_NIL;
	}
	@Override
	public void accept(T sample) {
		writer.write_untyped(sample, instance_handle);
	}
}
