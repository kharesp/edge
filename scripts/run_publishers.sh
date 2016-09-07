if [ "$#" -ne 1 ]; then
	echo "Usage: ./run_publishers.sh houseId"
	exit
fi

houseId=$1
dataPath="/home/pi/data/$houseId"
echo $dataPath

for entry in `ls $dataPath`; do 
	houseHold=$dataPath/$entry
	echo "Publishing data for houseHold id: $houseHold"
	./gradlew publisher --no-rebuild --no-daemon -PappArgs="['0','Readings','$houseHold']" > /dev/null 2>&1  &
done
