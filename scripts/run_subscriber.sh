houseId=$1
echo "Starting Subscriber for house id:$houseId"
java -cp build/libs/*:$EDGENT_HOME/target/java8/lib/*:$NDDSHOME/lib/java/nddsjava.jar com.rti.edge.analytics.Processor $houseId 60
