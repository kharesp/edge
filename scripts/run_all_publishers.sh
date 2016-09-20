for i in `seq 1 15`;
do
	echo "Starting publisher for house_id: $i and data file: data/$i.csv"
	java -cp build/libs/*:libs/*:$EDGENT_HOME/target/java8/lib/*:$NDDSHOME/lib/java/nddsjava.jar com.rti.edge.types.reading.ReadingPublisher 0 Readings data/$i.csv > pub$i.out 2>&1 &

done 
