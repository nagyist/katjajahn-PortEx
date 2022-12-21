package com.github.katjahahn.parser.sections.edata;

import com.github.katjahahn.TestreportsReader;
import com.github.katjahahn.parser.PEData;
import com.github.katjahahn.parser.PELoader;
import com.github.katjahahn.parser.PELoaderTest;
import com.github.katjahahn.parser.optheader.WindowsEntryKey;
import com.github.katjahahn.parser.sections.SectionLoader;
import com.google.common.base.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.testng.Assert.*;

public class ExportSectionTest {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager
            .getLogger(ExportSectionTest.class.getName());
    private Map<File, List<ExportEntry>> exportEntries;
    private Map<String, PEData> pedata = new HashMap<>();

    @BeforeClass
    public void prepare() throws IOException {
        exportEntries = TestreportsReader.readExportEntries();
        pedata = PELoaderTest.getPEData();
    }

    @Test
    public void forwarderTest() throws IOException {
        File forwarder = new File(TestreportsReader.RESOURCE_DIR
                + "/corkami/dllfw.dll");
        PEData data = PELoader.loadPE(forwarder);
        ExportSection edata = new SectionLoader(data).loadExportSection();
        List<ExportEntry> exportEntries = edata.getExportEntries();
        for (ExportEntry export : exportEntries) {
            assertTrue(export.forwarded());
        }

        File nonforwarder = new File(TestreportsReader.RESOURCE_DIR
                + "/corkami/exports_order.exe");
        data = PELoader.loadPE(nonforwarder);
        edata = new SectionLoader(data).loadExportSection();
        exportEntries = edata.getExportEntries();
        for (ExportEntry export : exportEntries) {
            assertFalse(export.forwarded());
        }

    }

    @Test
    public void getExportEntries() throws IOException {
        // assertEquals(pedata.size(), exportEntries.size()); TODO
        for (Entry<File, List<ExportEntry>> set : exportEntries.entrySet()) {
            File file = set.getKey();
            List<ExportEntry> expected = set.getValue();
            String filename = file.getName().replace(".txt", "");
            PEData datum = pedata.get(filename);
            SectionLoader loader = new SectionLoader(datum);
            Optional<ExportSection> edata = loader.maybeLoadExportSection();
            if (!edata.isPresent()) {
                assertTrue(expected.size() == 0);
            } else {
                expected = substractImageBase(expected, datum);
                List<ExportEntry> actual = edata.get().getExportEntries();
                assertEquals(actual, expected);
            }

        }
    }

    // Patches the expected list to match our RVA that has not the image base
    // added
    private List<ExportEntry> substractImageBase(List<ExportEntry> expected,
            PEData datum) {
        List<ExportEntry> list = new ArrayList<ExportEntry>();
        long imageBase = datum.getOptionalHeader().getWindowsFieldEntry(
                WindowsEntryKey.IMAGE_BASE).getValue();
        for (ExportEntry entry : expected) {
            if (entry instanceof ExportNameEntry) {
                list.add(new ExportNameEntry(entry.symbolRVA() - imageBase,
                        ((ExportNameEntry) entry).name(), entry.ordinal()));
            } else {
                list.add(new ExportEntry(entry.symbolRVA() - imageBase, entry
                        .ordinal()));
            }
        }
        return list;
    }
}
