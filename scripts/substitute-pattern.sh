#!/bin/bash

DIR=$(dirname $0)
CMD="$DIR/javaparser-transforms.sh"
CLASS="de.topobyte.javatransform.SubstitutePattern"

exec "$CMD" "$CLASS" "$@"
