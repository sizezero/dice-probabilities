#!/bin/bash

# it seems eclipse has problems importing a project of 
# same project name.  When working with tags and branches
# this is a pain.  This script initializes the project 
# with a potentially unique project name.

if [ $# != "1" ]; then
    echo "setup_eclipse.sh <project_name>"
    exit 1
fi

PNAME="$1"

cp .project.example .project
cp tests/.project.example tests/.project

perl -p -i -e "s/DiceProbabilities/$PNAME/g" .project tests/.project
