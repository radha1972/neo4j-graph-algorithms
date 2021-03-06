package org.neo4j.graphalgo.utils;

import org.neo4j.graphalgo.core.utils.paged.AllocationTracker;
import org.neo4j.graphalgo.core.utils.paged.LongArray;
import org.neo4j.graphalgo.core.utils.paged.SparseLongArray;
import org.neo4j.unsafe.impl.batchimport.cache.ChunkedHeapFactory;
import org.neo4j.unsafe.impl.batchimport.cache.DynamicLongArray;
import org.neo4j.unsafe.impl.batchimport.cache.OffHeapLongArray;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.Arrays;
import java.util.Random;

@State(Scope.Benchmark)
public class LongArrays {

    public enum Distribution {
        uniform, packed;
    }

    @Param({"100000", "10000000"})
    int size;

    @Param({"0.1", "0.5", "0.9", "0.999"})
    double sparseness;

    @Param({"uniform", "packed"})
    Distribution distribution;

    long[] primitive;
    LongArray paged;
    SparseLongArray sparse;
    OffHeapLongArray offHeap;
    DynamicLongArray chunked;

    @Setup
    public void setup() {
        primitive = createPrimitive(size, sparseness, distribution);
        paged = createPaged(primitive);
        sparse = createSparse(primitive);
        offHeap = createOffHeap(primitive);
        chunked = createChunked(primitive);
    }

    private static long[] createPrimitive(
            int size,
            double sparseness,
            Distribution distribution) {
        Random rand = new Random(0);
        long[] array = new long[size];
        if (distribution == Distribution.packed) {
            int blockSize = (int) Math.round(size * (1.0 - sparseness));
            int maxIndex = size - blockSize;
            int startIndex = rand.nextInt(maxIndex);
            Arrays.fill(array, -1L);
            for (int i = 0; i < blockSize; i++) {
                array[i + startIndex] = randomLong(rand);
            }
        } else {
            for (int i = 0; i < size; ++i) {
                if (rand.nextDouble() >= sparseness) {
                    array[i] = randomLong(rand);
                } else {
                    array[i] = -1L;
                }
            }
        }
        return array;
    }

    static LongArray createPaged(long[] values) {
        final LongArray array = LongArray.newArray(values.length, AllocationTracker.EMPTY);
        for (int i = 0; i < values.length; i++) {
            long value = values[i];
            if (value >= 0) {
                array.set(i, value);
            }
        }
        return array;
    }

    static SparseLongArray createSparse(long[] values) {
        final SparseLongArray array = SparseLongArray.newArray(values.length, AllocationTracker.EMPTY);
        for (int i = 0; i < values.length; i++) {
            long value = values[i];
            if (value >= 0) {
                array.set(i, value);
            }
        }
        return array;
    }

    static OffHeapLongArray createOffHeap(long[] values) {
        final OffHeapLongArray array = new OffHeapLongArray(values.length, -1L, 0);
        for (int i = 0; i < values.length; i++) {
            long value = values[i];
            if (value >= 0) {
                array.set(i, value);
            }
        }
        return array;
    }

    static DynamicLongArray createChunked(long[] values) {
        final DynamicLongArray array = ChunkedHeapFactory.newArray(values.length, -1L);
        for (int i = 0; i < values.length; i++) {
            long value = values[i];
            if (value >= 0) {
                array.set(i, value);
            }
        }
        return array;
    }

    private static long randomLong(Random random) {
        long v;
        do {
            v = random.nextLong();
        } while (v < 0);
        return v;
    }
}
