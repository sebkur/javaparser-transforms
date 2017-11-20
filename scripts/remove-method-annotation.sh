#!/bin/bash

DIR=$(dirname $0)
CMD="$DIR/javaparser-transforms.sh"
CLASS="de.topobyte.javatransform.RemoveMethodAnnotation"

exec "$CMD" "$CLASS" "$@"
