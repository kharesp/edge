

package com.rti.edge.types.houseAvg;

/* HouseAvgSubscriber.java

A publication of data of type HouseAvg

This file is derived from code automatically generated by the rtiddsgen 
command:

rtiddsgen -language java -example <arch> .idl

Example publication of type HouseAvg automatically generated by 
'rtiddsgen' To test them follow these steps:

(1) Compile this file and the example subscription.

(2) Start the subscription on the same domain used for RTI Data Distribution
Service with the command
java HouseAvgSubscriber <domain_id> <sample_count>

(3) Start the publication on the same domain used for RTI Data Distribution
Service with the command
java HouseAvgPublisher <domain_id> <sample_count>

(4) [Optional] Specify the list of discovery initial peers and 
multicast receive addresses via an environment variable or a file 
(in the current working directory) called NDDS_DISCOVERY_PEERS. 

You can run any number of publishers and subscribers programs, and can 
add and remove them dynamically from the domain.

Example:

To run the example application on domain <domain_id>:

Ensure that $(NDDSHOME)/lib/<arch> is on the dynamic library path for
Java.                       

On UNIX systems: 
add $(NDDSHOME)/lib/<arch> to the 'LD_LIBRARY_PATH' environment
variable

On Windows systems:
add %NDDSHOME%\lib\<arch> to the 'Path' environment variable

Run the Java applications:

java -Djava.ext.dirs=$NDDSHOME/class HouseAvgPublisher <domain_id>

java -Djava.ext.dirs=$NDDSHOME/class HouseAvgSubscriber <domain_id>  
*/

import java.util.Arrays;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.*;

// ===========================================================================

public class HouseAvgSubscriber {
	public static int domainId=0;
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        if (args.length < 2) {
        	System.out.println("Arguments: from_hId, to_hId");
        	return;
        }

        int from_hId = Integer.valueOf(args[0]).intValue();
        int to_hId = Integer.valueOf(args[1]).intValue();
        
        subscriberMain(from_hId, to_hId);
    }

    // -----------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------

    // --- Constructors: -----------------------------------------------------

    private HouseAvgSubscriber() {
        super();
    }

    // -----------------------------------------------------------------------

    private static void subscriberMain(int from_hId, int to_hId) {

        DomainParticipant participant = null;
        Subscriber subscriber = null;
        Topic topic = null;
        DataReaderListener listener = null;
        HouseAvgDataReader reader = null;

        try {

            // --- Create participant --- //

            /* To customize participant QoS, use
            the configuration file
            USER_QOS_PROFILES.xml */

            participant = DomainParticipantFactory.TheParticipantFactory.
            create_participant(
                domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (participant == null) {
                System.err.println("create_participant error\n");
                return;
            }                         

            // --- Create subscriber --- //

            /* To customize subscriber QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            subscriber = participant.create_subscriber(
                DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null /* listener */,
                StatusKind.STATUS_MASK_NONE);
            if (subscriber == null) {
                System.err.println("create_subscriber error\n");
                return;
            }     

            // --- Create topic --- //

            /* Register type before creating topic */
            String typeName = HouseAvgTypeSupport.get_type_name(); 
            HouseAvgTypeSupport.register_type(participant, typeName);

            /* To customize topic QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            topic = participant.create_topic(
                "HouseAvg",
                typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (topic == null) {
                System.err.println("create_topic error\n");
                return;
            }                     
            
            ContentFilteredTopic cft= participant.create_contentfilteredtopic("ContentFilteredTopic",
            		topic, "house_id >=%0 and house_id<=%1", new StringSeq(Arrays.asList(Integer.toString(from_hId),
            				Integer.toString(to_hId))));

            // --- Create reader --- //

            listener = new HouseAvgListener();

            /* To customize data reader QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            reader = (HouseAvgDataReader)
            subscriber.create_datareader(
                cft, Subscriber.DATAREADER_QOS_DEFAULT, listener,
                StatusKind.STATUS_MASK_ALL);
            if (reader == null) {
                System.err.println("create_datareader error\n");
                return;
            }                         

            // --- Wait for data --- //

            final long receivePeriodSec = 4;

            while(true){
                try {
                    Thread.sleep(receivePeriodSec * 1000);  // in millisec
                } catch (InterruptedException ix) {
                    System.err.println("INTERRUPTED");
                    break;
                }
            }
        } finally {

            // --- Shutdown --- //

            if(participant != null) {
                participant.delete_contained_entities();

                DomainParticipantFactory.TheParticipantFactory.
                delete_participant(participant);
            }
            /* RTI Data Distribution Service provides the finalize_instance()
            method for users who want to release memory used by the
            participant factory singleton. Uncomment the following block of
            code for clean destruction of the participant factory
            singleton. */
            //DomainParticipantFactory.finalize_instance();
        }
    }

    // -----------------------------------------------------------------------
    // Private Types
    // -----------------------------------------------------------------------

    // =======================================================================

    private static class HouseAvgListener extends DataReaderAdapter {

        HouseAvgSeq _dataSeq = new HouseAvgSeq();
        SampleInfoSeq _infoSeq = new SampleInfoSeq();

        public void on_data_available(DataReader reader) {
            HouseAvgDataReader HouseAvgReader =
            (HouseAvgDataReader)reader;

            try {
                HouseAvgReader.take(
                    _dataSeq, _infoSeq,
                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                    SampleStateKind.ANY_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE);

                for(int i = 0; i < _dataSeq.size(); ++i) {
                    SampleInfo info = (SampleInfo)_infoSeq.get(i);

                    if (info.valid_data) {
                        System.out.println(
                            ((HouseAvg)_dataSeq.get(i)).toString("Received",0));

                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No data to process
            } finally {
                HouseAvgReader.return_loan(_dataSeq, _infoSeq);
            }
        }
    }
}
