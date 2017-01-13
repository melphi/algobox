#!/bin/sh
set -e
gradle algobox-api:clean algobox-api:test algobox-api:shadowJar chdir=./java/algobox

