package com.rti.edge.edgent;

import java.util.LinkedList;
import org.apache.edgent.function.Consumer;
import org.apache.edgent.function.Supplier;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TypeSupportImpl;
import com.rti.dds.util.LoanableSequence;

public class DDSSubscriberConnector<T extends Copyable> implements Consumer<Consumer<T>>,Supplier<Iterable<T>>{
	private static final long serialVersionUID = 1L;
	private String topicName;
	private TypeSupportImpl typeSupport;
	private DomainParticipant participant;
	private Subscriber subscriber;
	private Topic topic;	
	private ContentFilteredTopic cft;
	private String cftStr;
	private StringSeq cftParams;
	private SampleInfoSeq infoSeq;
	private LoanableSequence dataSeq;
	private WaitSet waitset;
	private Duration_t wait_timeout;
	private StatusCondition status_condition;
	private DataReader dataReader;
	private Consumer<T> eventEmitter;


	public DDSSubscriberConnector(String topicName,TypeSupportImpl typeSupport) throws Exception
	{
		this(topicName,typeSupport,null,null);
	}
	public DDSSubscriberConnector(String topicName,TypeSupportImpl typeSupport,
			String cftStr,StringSeq params)throws Exception{
		
		this.topicName= topicName;
		this.typeSupport=typeSupport;
		if(cftStr!=null){
			this.cftStr=cftStr;
			this.cftParams=params;
		}
		try{
			participant=DefaultParticipant.instance();
			DomainParticipantQos pQos= new DomainParticipantQos();
			participant.get_qos(pQos);
			System.out.println("rtps_host_id:"+pQos.wire_protocol.rtps_host_id);
		}catch(Exception e){
			throw e; 
		}
		initialize();
	}

	@Override
	public void accept(Consumer<T> eventEmitter) {
		this.eventEmitter=eventEmitter;
		installListener();
	}
	private void installListener(){
		DataReaderListener listener = new DataReaderListener();
		dataReader.set_listener(listener,StatusKind.STATUS_MASK_ALL);
	}
	private void initialize() throws Exception{
		subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (subscriber == null) {
			throw new Exception("create_subscriber error");
		}
		DefaultParticipant.registerType(typeSupport);

		topic = participant.create_topic(topicName, typeSupport.get_type_nameI(), DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (topic == null) {
			throw new Exception("create_topic error");
		}
		if(cftStr!=null){
			cft=participant.create_contentfilteredtopic("ContentFilteredTopic", topic, cftStr,cftParams);
			dataReader = subscriber.create_datareader(cft, Subscriber.DATAREADER_QOS_DEFAULT, null,
					StatusKind.STATUS_MASK_ALL);
			if (dataReader == null) {
				throw new Exception("create_datareader error");
			}
		}else{
			dataReader = subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null,
					StatusKind.STATUS_MASK_ALL);
			if (dataReader == null) {
				throw new Exception("create_datareader error");
			}
		}
		DataReaderQos rQos= new DataReaderQos();
		participant.get_default_datareader_qos(rQos);
		System.out.println("Default dr qos:"+rQos.history.kind);
		System.out.println("Default dr qos:"+rQos.reliability.kind);

		
		dataSeq= new LoanableSequence(typeSupport.get_type());
		infoSeq = new SampleInfoSeq();
		status_condition = dataReader.get_statuscondition();
		status_condition.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		waitset = new WaitSet();
		waitset.attach_condition(status_condition);
		wait_timeout = new Duration_t();
		wait_timeout.sec = 10;
		wait_timeout.nanosec = 0;
	}
	private class DataReaderListener extends DataReaderAdapter {

		public void on_data_available(DataReader reader) {
			take().forEach(t -> eventEmitter.accept(t));
		}
	}
	
	
	@Override
	public Iterable<T> get() {
		Iterable<T> data=null;
		ConditionSeq active_conditions_seq = new ConditionSeq();
		
		try {
			waitset.wait(active_conditions_seq, wait_timeout);
		} catch (RETCODE_TIMEOUT e) {
			System.out.println("Wait timed out!! No conditions were triggered.\n");
		}
		for (int i = 0; i < active_conditions_seq.size(); ++i) {

			if (active_conditions_seq.get(i) == status_condition) {
				int triggeredmask = dataReader.get_status_changes();
				if ((triggeredmask & StatusKind.DATA_AVAILABLE_STATUS) != 0) {
						data=take();
				}
			}
		}
		return data;
	}
	
	@SuppressWarnings("unchecked") 
	Iterable<T> take(){
		LinkedList<T> data= new LinkedList<T>();
		try {
			dataReader.take_untyped(dataSeq, infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
					SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
					InstanceStateKind.ANY_INSTANCE_STATE);

			for (int j = 0; j < dataSeq.size(); ++j) {
				if (((SampleInfo) infoSeq.get(j)).valid_data) {
					T sample= (T) dataSeq.get(j);
					T dataCopy= (T) typeSupport.create_data();
					dataCopy.copy_from(sample);
					data.addLast(dataCopy);
				}
			}
		} catch (RETCODE_NO_DATA noData)
		{
		} finally {
			dataReader.return_loan_untyped(dataSeq, infoSeq);
		}
		return data;
	}

}
