for i in `seq 1 15`;
do
	echo "Starting subscriber for house_id: $i"
	java -cp build/libs/*:$EDGENT_HOME/target/java8/lib/*:$NDDSHOME/lib/java/nddsjava.jar com.rti.edge.analytics.Processor $i 60 > sub$i.out 2>&1 &
done 
