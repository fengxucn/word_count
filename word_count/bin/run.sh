#!/usr/bin/env bash

get_abs_script_path() {
  pushd . >/dev/null
  cd "$(dirname "$0")"
  appdir=$(pwd)
  popd  >/dev/null
}

get_abs_script_path

java -jar $appdir/../lib/word_count.jar $appdir/../config/config.properties