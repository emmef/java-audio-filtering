#!/bin/bash

clean="clean"

while [ -n "$1" ] ; do
  argument="$1"
  skip
  case "$1" in
    --no-clean)
      clean=
    ;;
  esac
done

if [ -x "$(which maven)" ] ; then
  maven="maven"
else
  maven="mvn"
fi

${maven} ${clean} compile assembly:single
