#!/bin/bash

DIR=$(dirname $0)
CMD="$DIR/javaparser-transform-tests.sh"
CLASS="de.topobyte.javatransform.RemoveMethod"

exec "$CMD" "$CLASS" "$@"
