package com.jdurham.broadcast;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopologyHandlerTest {

    //@Test
    void constructTopology() {
        List<String> nodeIds = IntStream.range(0, 25)
                .mapToObj(i -> String.format("n%d", i))
                .toList();

        Map<String, List<String>> topology = TopologyHandler.constructTopology(nodeIds);
        assertEquals(List.of("n11", "n12", "n2"), topology.get("n5"));
    }
}