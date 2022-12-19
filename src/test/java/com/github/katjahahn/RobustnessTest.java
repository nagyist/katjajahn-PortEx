package com.github.katjahahn;

import static org.testng.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.katjahahn.parser.optheader.OptionalHeaderTest;
import com.github.katjahahn.parser.sections.rsrc.icon.IconParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import com.github.katjahahn.parser.PEData;
import com.github.katjahahn.parser.PELoader;
import com.github.katjahahn.parser.sections.SectionLoader;
import com.github.katjahahn.parser.sections.idata.ImportDLL;
import com.github.katjahahn.tools.anomalies.PEAnomalyScannerTest;

public class RobustnessTest {

    public static final String PROBLEMFILES_DIR = TestreportsReader.RESOURCE_DIR
            + "/corkami/";

    @Test
    public void loadTinyPE() throws IOException {
        File tinyest = new File(PEAnomalyScannerTest.UNUSUAL_FOLDER
                + "/tinype/tinyest.exe");
        PEData data = PELoader.loadPE(tinyest);
        assertEquals(data.getSectionTable().getNumberOfSections(), 1);
        assertTrue(data.getOptionalHeader().getDataDirectory().isEmpty());

        File downloader = new File(PEAnomalyScannerTest.UNUSUAL_FOLDER
                + "/tinype/downloader.exe");
        data = PELoader.loadPE(downloader);
        List<ImportDLL> imports = new SectionLoader(data).loadImportSection()
                .getImports();
        assertFalse(imports.isEmpty());
        assertTrue(imports.get(0).getName().equals("\\\\66.93.68.6\\z"));
    }

    @Test
    public void loadProblemfiles() throws IOException {
        File folder = new File(PROBLEMFILES_DIR);
        for (File file : folder.listFiles()) {
            logger.debug("loading problem file: " + file.getAbsolutePath());
            PEData data = PELoader.loadPE(file);
            SectionLoader loader = new SectionLoader(data);
            loader.maybeLoadBoundImportSection();
            loader.maybeLoadCLRSection();
            loader.maybeLoadDebugSection();
            loader.maybeLoadDelayLoadSection();
            loader.maybeLoadExceptionSection();
            loader.maybeLoadExportSection();
            loader.maybeLoadImportSection();
            loader.maybeLoadResourceSection();
            data.maybeGetRichHeader();
            IconParser.extractIcons(data);
        }
    }

    private static Logger logger = LogManager.getLogger(OptionalHeaderTest.class.getName());

}
