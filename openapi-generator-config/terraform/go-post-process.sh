set -x
#regex='(.*)api_(.*_resource|.*_data_source).go'

# Check if the file is empty
if [ -z "$(cat "$1")" ]; then
  echo "Removing empty file $1"
  rm "$1"
else
  goimports -w "$1"
#  [[ $1 =~ $regex ]] && mv "$1" "${BASH_REMATCH[1]}${BASH_REMATCH[2]}.go"
fi


