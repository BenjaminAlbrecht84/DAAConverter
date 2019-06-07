/*
 * Copyright 2017 Benjamin Albrecht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class DaaReader {

    private File daaFile;
    private boolean verbose = false;

    private DaaHeader header;
    private CountDownLatch latch;
    private ConcurrentHashMap<String, ArrayList<DaaHit>> readId2Hits;

    private AtomicLong allParsedRecords;
    ;
    private int last_p;
    private long numOfRecords;

    public DaaReader(File daaFile, Long offset, boolean verbose) {
        this.verbose = verbose;
        this.daaFile = daaFile;
        header = new DaaHeader(daaFile, offset == null ? 0 : offset);
        header.loadAllReferences();
        if (verbose)
            header.print();
    }

    public ArrayList<DaaHit> parseAllHits(int cores) {

        System.out.println("STEP_3>Parsing DIAMOND output...");
        long time = System.currentTimeMillis();

        readId2Hits = new ConcurrentHashMap<String, ArrayList<DaaHit>>();

        System.out.println("OUTPUT>Parsing " + header.getNumberOfQueryRecords() + " query records...");

        last_p = 0;
        allParsedRecords = new AtomicLong(0);
        numOfRecords = header.getNumberOfQueryRecords();

        int chunk = (int) Math.ceil((double) header.getNumberOfQueryRecords() / (double) cores);
        Vector<Thread> allParser = new Vector<Thread>();
        for (int l = 0; l < header.getNumberOfQueryRecords(); l += chunk) {
            int r = (l + chunk) < header.getNumberOfQueryRecords() ? l + chunk : (int) header.getNumberOfQueryRecords();
            int[] bounds = {l, r};
            DaaParser parser = new DaaParser(bounds, true);
            allParser.add(parser);
        }

        latch = new CountDownLatch(allParser.size());
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        for (Thread thread : allParser)
            executor.execute(thread);

        // awaiting termination
        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        executor.shutdown();

        long runtime = (System.currentTimeMillis() - time) / 1000;
        System.out.println("OUTPUT>" + 100 + "% (" + numOfRecords + "/" + numOfRecords + ") of all records parsed.[" + runtime + "s]\n");

        ArrayList<DaaHit> result = new ArrayList<>();
        for (ArrayList<DaaHit> value : readId2Hits.values())
            result.addAll(value);
        return result;

    }

    private synchronized void reportProgress(int p) {
        p = ((int) Math.floor((double) p / 10.)) * 10;
        if (p != 100 && p != last_p && p % 1 == 0) {
            System.out.println("OUTPUT>" + p + "% (" + allParsedRecords + "/" + numOfRecords + ") of all records parsed.");
            last_p = p;
        }
    }

    private synchronized void addHits(HashMap<String, ArrayList<DaaHit>> localReadMap) {
        for (String read_id : localReadMap.keySet()) {
            if (!readId2Hits.containsKey(read_id))
                readId2Hits.put(read_id, new ArrayList<DaaHit>());
            readId2Hits.get(read_id).addAll(localReadMap.get(read_id));
        }
    }

    public class DaaParser extends Thread {

        private int[] bounds;
        private boolean parseAll;
        private HashMap<String, ArrayList<DaaHit>> localReadMap;

        public DaaParser(int[] queryRecordsBounds, boolean parseAll) {
            this.bounds = queryRecordsBounds;
            this.parseAll = parseAll;
        }

        public void run() {

            localReadMap = new HashMap<String, ArrayList<DaaHit>>();

            try {

                RandomAccessFile raf = new RandomAccessFile(daaFile, "r");

                try {

                    raf.seek(header.getLocationOfBlockInFile(header.getAlignmentsBlockIndex()));
                    for (int i = 0; i < header.getNumberOfQueryRecords(); i++) {

                        DaaHit hit = new DaaHit();

                        long filePointer = raf.getFilePointer();
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        buffer.order(ByteOrder.LITTLE_ENDIAN);
                        raf.read(buffer.array());
                        int alloc = buffer.getInt();

                        ByteBuffer hitBuffer = ByteBuffer.allocate(alloc);
                        hitBuffer.order(ByteOrder.LITTLE_ENDIAN);
                        raf.read(hitBuffer.array());

                        if (i >= bounds[0] && i < bounds[1]) {

                            // parsing query properties
                            hit.parseQueryProperties(filePointer, hitBuffer, true, true);

                            while (hitBuffer.position() < hitBuffer.capacity()) {

                                // parsing match properties
                                DaaHit h = new DaaHit();
                                h.copyQueryProperties(hit);
                                h.parseHitProperties(header, hitBuffer, true);
                                String readName = h.getQueryName();
                                localReadMap.putIfAbsent(readName, new ArrayList<DaaHit>());
                                localReadMap.get(readName).add(h);

                            }

                            if (i != 0 && i % 1000 == 0) {
                                int p = (int) Math.round(((double) allParsedRecords.addAndGet(1000) / (double) numOfRecords) * 100.);
                                reportProgress(p);
                            }

                        }

                        if (i >= bounds[1])
                            break;

                    }

                } finally {
                    raf.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (parseAll) {
                addHits(localReadMap);
                latch.countDown();
            }

        }

        public HashMap<String, ArrayList<DaaHit>> getLocalReadMap() {
            return localReadMap;
        }

    }

    public DaaHeader getHeader() {
        return header;
    }
}
