package com.rti.edge.edgent;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.TypeSupportImpl;

public class DefaultParticipant {
	private static DomainParticipant instance;
	private static int domainId=0;

	public static DomainParticipant instance() throws Exception {
		if (instance==null){
			instance= DomainParticipantFactory.get_instance().create_participant(domainId,
					DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
			if(instance==null)
				throw new Exception("create_participant error");
		}
		return instance;
	}

	public static void shutdown()
	{
		if(instance!=null){
			instance.delete_contained_entities();
			DomainParticipantFactory.get_instance().delete_participant(instance);
		}
	}

	public static void  registerType(String typeName,TypeSupportImpl typeSupportClass) throws Exception{
		typeSupportClass.register_typeI(instance(), typeName);
	}

}
