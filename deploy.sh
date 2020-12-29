#!/bin/bash
BINTRAY_USER=$1
BINTRAY_API_KEY=$2

function show_help_and_exit() {
	echo "Usage:"
	echo " deploy.sh bintrayUser apiKey"
	exit 1
}

if [ -z "$BINTRAY_USER" ]; then
	echo "bintray user not set"
	show_help_and_exit;
fi
if [ -z "$BINTRAY_API_KEY" ]; then
	echo "bintray api key not set"
	show_help_and_exit;
fi

# call jar signing before pushing to bintray...
export KEYSTORE_PWD
export KEYSTORE_LOCATION

set -e

./signJars

./pushToBintray.sh ${BINTRAY_USER} ${BINTRAY_API_KEY} de-jcup ekube update-site 0.8.0 ./ekube-updatesite
