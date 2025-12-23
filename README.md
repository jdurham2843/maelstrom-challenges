Challenges found here: https://fly.io/dist-sys/

Echo - Run this:
./maelstrom test -w echo --bin runner.sh --node-count 1 --time-limit 10

Unique ID:
./maelstrom test -w unique-ids --bin runner.sh --time-limit 30 --rate 1000 --node-count 3 --availability total --nemesis partition

Single Node Broadcast:
./maelstrom test -w broadcast --bin runner.sh --node-count 1 --time-limit 20 --rate 10

Multi node broadcast:
./maelstrom test -w broadcast --bin runner.sh --node-count 5 --time-limit 20 --rate 10

Fault tolerant broadcast:
./maelstrom test -w broadcast --bin runner.sh --node-count 5 --time-limit 20 --rate 10 --nemesis partition

[Efficient, fault tolerant broadcast (pt1)](https://fly.io/dist-sys/3d/):
- specification:
  - Messages-per-operation is below 30
  - Median latency is below 400ms
  - Maximum latency is below 600ms

Starting:

:stable-latencies {0 0, 0.5 454, 0.95 684, 0.99 776, 1 804}  
:net {:all {:send-count 166462, :recv-count 166462, :msg-count 166462, :msgs-per-op 87.79642}

./maelstrom test -w broadcast --bin runner.sh --node-count 25 --time-limit 20 --rate 100 --latency 100