#!/bin/bash

echo
echo "################################"
echo "# Prepare for deployment"
echo "################################"
echo 
# call copyright script etc. 
# its outside the root folder because of 
# problems with github license autodection...
./other/apply_copyright_info.sh
./other/apply_version_info.sh


