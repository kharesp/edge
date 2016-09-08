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
	private TypeSupportImpl typeSupport;
	private DomainParticipant participant;
	private Publisher publisher;
	private Topic topic;
	private DataWriter writer;
	private InstanceHandle_t instance_handle;
	
	
	public DDSPublisherConnector(String topicName, TypeSupportImpl typeSupport) throws Exception
	{
		this.topicName= topicName;
		this.typeSupport=typeSupport;
		try{
			participant=DefaultParticipant.instance();
		}catch(Exception e){
			throw e;
		}
		initialize();
	}

	private void initialize() throws Exception{
		publisher = participant.create_publisher(
				DomainParticipant.PUBLISHER_QOS_DEFAULT,
				null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (publisher == null) {
			throw new Exception("create_publisher error\n");
		}

		DefaultParticipant.registerType(typeSupport);

		topic = participant.create_topic(topicName,typeSupport.get_type_nameI(),
				DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (topic == null) {
			throw new Exception("create_topic error\n");
		}

		writer = publisher.create_datawriter(
				topic, 
				Publisher.DATAWRITER_QOS_DEFAULT,
				null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (writer == null) {
			throw new Exception("create_datawriter error\n");
		}
		instance_handle= InstanceHandle_t.HANDLE_NIL;
	}
	@Override
	public void accept(T sample) {
		writer.write_untyped(sample, instance_handle);
	}
}
