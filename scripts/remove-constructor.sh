#!/bin/bash

DIR=$(dirname $0)
CMD="$DIR/javaparser-transforms.sh"
CLASS="de.topobyte.javatransform.RemoveConstructor"

exec "$CMD" "$CLASS" "$@"
