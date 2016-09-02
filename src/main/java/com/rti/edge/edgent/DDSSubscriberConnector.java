package com.rti.edge.edgent;

import java.util.LinkedList;
import org.apache.edgent.function.Consumer;
import org.apache.edgent.function.Supplier;
import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusCondition;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TypeSupportImpl;
import com.rti.dds.util.LoanableSequence;

public class DDSSubscriberConnector<T extends Copyable> implements Consumer<Consumer<T>>,Supplier<Iterable<T>>{
	private static final long serialVersionUID = 1L;
	private String topicName;
	private Class<T> typeClass;
	private TypeSupportImpl typeSupport;
	private DomainParticipant participant;
	private Subscriber subscriber;
	private Topic topic;	
	private SampleInfoSeq infoSeq;
	private LoanableSequence dataSeq;
	private WaitSet waitset;
	private StatusCondition status_condition;
	private DataReader dataReader;
	private Consumer<T> eventEmitter;


	public DDSSubscriberConnector(String topicName, Class<T> typeClass, TypeSupportImpl typeSupport)
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

	@Override
	public void accept(Consumer<T> eventEmitter) {
		this.eventEmitter=eventEmitter;
		installListener();
	}
	private void installListener(){
		DataReaderListener listener = new DataReaderListener();
		dataReader.set_listener(listener,StatusKind.STATUS_MASK_ALL);
	}
	private void initialize(){
		subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
				StatusKind.STATUS_MASK_NONE);
		if (subscriber == null) {
			System.err.println("create_subscriber error\n");
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
		dataReader= subscriber.create_datareader(topic,Subscriber.DATAREADER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_ALL);
		dataSeq= new LoanableSequence(typeClass);
		infoSeq = new SampleInfoSeq();
		status_condition = dataReader.get_statuscondition();
		status_condition.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
		waitset = new WaitSet();
		waitset.attach_condition(status_condition);
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
		Duration_t wait_timeout = new Duration_t();
		wait_timeout.sec = 10;
		wait_timeout.nanosec = 0;
		try {
			waitset.wait(active_conditions_seq, wait_timeout);
		} catch (RETCODE_TIMEOUT e) {
			System.out.println("Wait timed out!! No conditions were triggered.\n");
		}
		System.out.print("Got " + active_conditions_seq.size() + " active conditions\n");
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
					T newData= (T) typeSupport.create_data();
					newData.copy_from(sample);
					data.addLast(newData);
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
