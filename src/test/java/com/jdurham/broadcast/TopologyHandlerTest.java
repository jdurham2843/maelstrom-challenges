package com.jdurham.broadcast;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TopologyHandlerTest {

    @Test
    void constructTopology() {
        List<String> nodeIds = IntStream.range(0, 25)
                .mapToObj(i -> String.format("n%d", i))
                .toList();

        Map<String, List<String>> topology = TopologyHandler.constructTopology(nodeIds);
        assertEquals(List.of("n7", "n17", "n11", "n13"), topology.get("n12"));
    }
}