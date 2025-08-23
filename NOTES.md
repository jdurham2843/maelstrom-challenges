Echo - Run this:
./maelstrom test -w echo --bin runner.sh --node-count 1 --time-limit 10

Unique ID:
./maelstrom test -w unique-ids --bin runner.sh --time-limit 30 --rate 1000 --node-count 3 --availability total --nemesis partition

Single Node Broadcast:
./maelstrom test -w broadcast --bin runner.sh --node-count 1 --time-limit 20 --rate 10

Multi node broadcast:
./maelstrom test -w broadcast --bin runner.sh --node-count 5 --time-limit 20 --rate 10
