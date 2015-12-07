redis-server &
REDIS_PID=$!
sleep 2
for dir in ./*; do
    if test -d "$dir"; then
        cd $dir
        activator clean | grep -v info
        activator test | grep -v info
        cd ..
    fi
done
kill $REDIS_PID
