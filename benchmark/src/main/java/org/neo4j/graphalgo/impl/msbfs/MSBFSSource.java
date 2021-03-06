/**
 * Copyright (c) 2017 "Neo4j, Inc." <http://neo4j.com>
 *
 * This file is part of Neo4j Graph Algorithms <http://github.com/neo4j-contrib/neo4j-graph-algorithms>.
 *
 * Neo4j Graph Algorithms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.graphalgo.impl.msbfs;

import org.neo4j.graphalgo.api.HugeIdMapping;
import org.neo4j.graphalgo.api.HugeRelationshipConsumer;
import org.neo4j.graphalgo.api.HugeRelationshipIterator;
import org.neo4j.graphalgo.api.IdMapping;
import org.neo4j.graphalgo.api.RelationshipConsumer;
import org.neo4j.graphalgo.api.RelationshipIterator;
import org.neo4j.graphalgo.core.huge.HugeDirectIdMapping;
import org.neo4j.graphalgo.core.neo4jview.DirectIdMapping;
import org.neo4j.graphdb.Direction;

import java.util.Arrays;

public enum MSBFSSource {

    _1024_32(1024, 32),
    _1024_128(1024, 128),
    _1024_1024(1024, 1024),
    _1024(1024),

    _8192_32(8192, 32),
    _8192_128(8192, 128),
    _8192_1024(8192, 1024),
    _8192_8192(8192, 8192),
    _8192(8192),

    _16384_32(16384, 32),
    _16384_128(16384, 128),
    _16384_1024(16384, 1024),
    _16384_8192(16384, 8192),
    _16384_16384(16384, 16384),
    _16384(16384);

    final IdMapping nodes;
    final HugeIdMapping hugeNodes;
    final RelationshipIterator rels;
    final HugeRelationshipIterator hugeRels;
    final int[] sources;
    final long[] hugeSources;

    MSBFSSource(int nodeCount, int sourceCount) {
        this.nodes = new DirectIdMapping(nodeCount);
        this.hugeNodes = new HugeDirectIdMapping(nodeCount);
        this.rels = new AllNodes(nodeCount);
        this.hugeRels = new HugeAllNodes(nodeCount);
        this.sources = new int[sourceCount];
        this.hugeSources = new long[sourceCount];
        Arrays.setAll(sources, i -> i);
        Arrays.setAll(hugeSources, i -> i);
    }

    MSBFSSource(int nodeCount) {
        this.nodes = new DirectIdMapping(nodeCount);
        this.hugeNodes = new HugeDirectIdMapping(nodeCount);
        this.rels = new AllNodes(nodeCount);
        this.hugeRels = new HugeAllNodes(nodeCount);
        this.sources = null;
        this.hugeSources = null;
    }

    private static final class AllNodes implements RelationshipIterator {

        private final int nodeCount;

        private AllNodes(final int nodeCount) {
            this.nodeCount = nodeCount;
        }

        @Override
        public void forEachRelationship(
                int nodeId,
                Direction direction,
                RelationshipConsumer consumer) {
            for (int i = 0; i < nodeCount; i++) {
                if (i != nodeId) {
                    consumer.accept(nodeId, i, -1L);
                }
            }
        }
    }

    private static final class HugeAllNodes implements HugeRelationshipIterator {

        private final long nodeCount;

        private HugeAllNodes(final long nodeCount) {
            this.nodeCount = nodeCount;
        }

        @Override
        public void forEachRelationship(
                long nodeId,
                Direction direction,
                HugeRelationshipConsumer consumer) {
            for (long i = 0; i < nodeCount; i++) {
                if (i != nodeId) {
                    consumer.accept(nodeId, i);
                }
            }
        }
    }
}
