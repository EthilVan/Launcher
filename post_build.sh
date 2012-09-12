version_line=$(cat pom.xml | egrep -o "<version>(.+)</version>" | head -n 1)
version=$(expr match $version_line "<version>\(.\+\)</version>")
if [[ $version == *-SNAPSHOT ]]; then
    name="EthilVanDev"
else
    name="EthilVan"
fi
extension="jar"

cp $WORKSPACE/target/EthilVan.$extension $DEPLOY_PATH/$name.$extension
