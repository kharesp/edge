package com.rti.edge.types.reading;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.*;

public class ReadingSubscriber {

	private static final int DOMAIN_ID = 0;
	private static final String TOPIC = "Readings";

	public static void main(String[] args) {

		int domainId = DOMAIN_ID;
		if (args.length >= 1) {
			domainId = Integer.valueOf(args[0]).intValue();
		}

		// -- Get max loop count; 0 means infinite loop --- //
		int sampleCount = 0;
		if (args.length >= 2) {
			sampleCount = Integer.valueOf(args[1]).intValue();
		}

		//subscriberMain(domainId, sampleCount);
		subscribeWithWaitsets(domainId,sampleCount);
	}

	public static void subscribeWithWaitsets(int domainId, int sampleCount) {
		DomainParticipant participant = null;
		Subscriber subscriber = null;
		Topic topic = null;
		ReadingDataReader reader = null;
		try {
			participant = DomainParticipantFactory.TheParticipantFactory.create_participant(domainId,
					DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (participant == null) {
				System.err.println("create_participant error\n");
				return;
			}
			subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (subscriber == null) {
				System.err.println("create_subscriber error\n");
				return;
			}
			String typeName = ReadingTypeSupport.get_type_name();
			ReadingTypeSupport.register_type(participant, typeName);

			topic = participant.create_topic(TOPIC, typeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (topic == null) {
				System.err.println("create_topic error\n");
				return;
			}
			reader = (ReadingDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, null,
					StatusKind.STATUS_MASK_ALL);
			if (reader == null) {
				System.err.println("create_datareader error\n");
				return;
			}
			StatusCondition status_condition = reader.get_statuscondition();
			if (status_condition == null) {
				System.err.println("get_statuscondition error\n");
				return;
			}
			status_condition.set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);
			WaitSet waitset = new WaitSet();
			waitset.attach_condition(status_condition);
			final long receivePeriodSec = 1;

			for (int count = 0; (sampleCount == 0) || (count < sampleCount); ++count) {
				ConditionSeq active_conditions_seq = new ConditionSeq();
				Duration_t wait_timeout = new Duration_t();
				wait_timeout.sec = 1;
				wait_timeout.nanosec = 500000000;
				try {
					waitset.wait(active_conditions_seq, wait_timeout);
				} catch (RETCODE_TIMEOUT e) {
					System.out.println("Wait timed out!! No conditions were triggered.\n");
					continue;
				}
				System.out.print("Got " + active_conditions_seq.size() + " active conditions\n");
				for (int i = 0; i < active_conditions_seq.size(); ++i) {

					if (active_conditions_seq.get(i) == status_condition) {
						int triggeredmask = reader.get_status_changes();
						if ((triggeredmask & StatusKind.DATA_AVAILABLE_STATUS) != 0) {
							ReadingSeq data_seq = new ReadingSeq();
							SampleInfoSeq info_seq = new SampleInfoSeq();
							boolean follow = true;
							while (follow) {
								try {
									reader.take(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
											SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
											InstanceStateKind.ANY_INSTANCE_STATE);

									/* Print data */
									for (int j = 0; j < data_seq.size(); ++j) {
										if (!((SampleInfo) info_seq.get(j)).valid_data) {
											System.out.println("Got metadata");
											continue;
										}
										System.out.println(((Reading) data_seq.get(j)).toString());
									}
								} catch (RETCODE_NO_DATA noData) {
									/*
									 * When there isn't data, the subscriber
									 * stop to take samples
									 */
									follow = false;
								} finally {
									/* Return the loaned data */
									reader.return_loan(data_seq, info_seq);
								}
							}
						}
					}
				}
				try {
					Thread.sleep(receivePeriodSec * 1000); // in millisec
				} catch (InterruptedException ix) {
					System.err.println("INTERRUPTED");
					break;
				}
			}

		} finally {
			if (participant != null) {
				participant.delete_contained_entities();
				DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
			}
		}
	}

	public static void subscriberMain(int domainId, int sampleCount) {

		DomainParticipant participant = null;
		Subscriber subscriber = null;
		Topic topic = null;
		DataReaderListener listener = null;
		ReadingDataReader reader = null;

		try {

			participant = DomainParticipantFactory.TheParticipantFactory.create_participant(domainId,
					DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);
			if (participant == null) {
				System.err.println("create_participant error\n");
				return;
			}

			subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (subscriber == null) {
				System.err.println("create_subscriber error\n");
				return;
			}

			/* Register type before creating topic */
			String typeName = ReadingTypeSupport.get_type_name();
			ReadingTypeSupport.register_type(participant, typeName);

			topic = participant.create_topic(TOPIC, typeName, DomainParticipant.TOPIC_QOS_DEFAULT, null /* listener */,
					StatusKind.STATUS_MASK_NONE);
			if (topic == null) {
				System.err.println("create_topic error\n");
				return;
			}

			listener = new ReadingListener();

			reader = (ReadingDataReader) subscriber.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT,
					listener, StatusKind.STATUS_MASK_ALL);
			if (reader == null) {
				System.err.println("create_datareader error\n");
				return;
			}

			final long receivePeriodSec = 1;

			for (int count = 0; (sampleCount == 0) || (count < sampleCount); ++count) {
				try {
					Thread.sleep(receivePeriodSec * 1000); // in millisec
				} catch (InterruptedException ix) {
					System.err.println("INTERRUPTED");
					break;
				}

			}
		} finally {

			if (participant != null) {
				participant.delete_contained_entities();

				DomainParticipantFactory.TheParticipantFactory.delete_participant(participant);
			}
		}
	}

	private static class ReadingListener extends DataReaderAdapter {

		ReadingSeq _dataSeq = new ReadingSeq();
		SampleInfoSeq _infoSeq = new SampleInfoSeq();

		public void on_data_available(DataReader reader) {
			ReadingDataReader ReadingReader = (ReadingDataReader) reader;

			try {
				ReadingReader.take(_dataSeq, _infoSeq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
						SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
						InstanceStateKind.ANY_INSTANCE_STATE);

				for (int i = 0; i < _dataSeq.size(); ++i) {
					SampleInfo info = (SampleInfo) _infoSeq.get(i);

					if (info.valid_data) {
						System.out.println(((Reading) _dataSeq.get(i)).toString("Received", 0));

					}
				}
			} catch (RETCODE_NO_DATA noData) {
				// No data to process
			} finally {
				ReadingReader.return_loan(_dataSeq, _infoSeq);
			}
		}
	}
}
