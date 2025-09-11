if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  alias builder=$PWD/utils/builder/bin/linux/builder
elif [[ "$OSTYPE" == "darwin"* ]]; then
  alias builder=$PWD/utils/builder/bin/darwin/builder
else
  echo "sorry bucko, you're outta luck"
fi


