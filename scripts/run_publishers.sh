h_id=$1
dataFile="../"$2
echo "Publishing data for house id: $h_id"
./gradlew publisher --no-rebuild --no-daemon -PappArgs="['0','Readings','$dataFile']"
