/*******************************************************************************
 * Copyright 2014 Katja Hahn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.github.katjahahn;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.katjahahn.coffheader.COFFFileHeader;
import com.github.katjahahn.msdos.MSDOSHeader;
import com.github.katjahahn.optheader.OptionalHeader;
import com.github.katjahahn.sections.SectionLoader;
import com.github.katjahahn.sections.SectionTable;

/**
 * Loads PEData of a file. Spares the user of the library to collect every
 * information necessary.
 * 
 * @author Katja Hahn
 * 
 */
public class PELoader {
	
	private static final Logger logger = LogManager.getLogger(PELoader.class.getName());

	private final File file;

	private PELoader(File file) {
		this.file = file;
	}

	/**
	 * Loads the basic data for the given PE file.
	 * 
	 * @param peFile
	 * @return data of the PE file
	 * @throws IOException
	 */
	public static PEData loadPE(File peFile) throws IOException {
		return new PELoader(peFile).loadData();
	}

	private PEData loadData() throws IOException {
		PESignature pesig = new PESignature(file);
		pesig.read();
		MSDOSHeader msdos = null;
		COFFFileHeader coff = null;
		OptionalHeader opt = null;
		SectionTable table = null;
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
			msdos = loadMSDOSHeader(raf);
			msdos.read();
			coff = loadCOFFFileHeader(pesig, raf);
			coff.read();
			opt = loadOptionalHeader(pesig, coff, raf);
			opt.read();
			table = loadSectionTable(pesig, coff, raf);
			table.read();
		}
		return new PEData(msdos, pesig, coff, opt, table, file);
	}

	private MSDOSHeader loadMSDOSHeader(RandomAccessFile raf)
			throws IOException {
		byte[] headerbytes = loadBytes(0, MSDOSHeader.FORMATTED_HEADER_SIZE,
				raf);
		return new MSDOSHeader(headerbytes);
	}

	private SectionTable loadSectionTable(PESignature pesig,
			COFFFileHeader coff, RandomAccessFile raf) throws IOException {
		long offset = pesig.getPEOffset() + PESignature.PE_SIG_LENGTH
				+ COFFFileHeader.HEADER_SIZE + coff.getSizeOfOptionalHeader();
		int numberOfEntries = coff.getNumberOfSections();
		byte[] tableBytes = loadBytes(offset, SectionTable.ENTRY_SIZE
				* numberOfEntries, raf);
		return new SectionTable(tableBytes, numberOfEntries);
	}

	private COFFFileHeader loadCOFFFileHeader(PESignature pesig,
			RandomAccessFile raf) throws IOException {
		long offset = pesig.getPEOffset() + PESignature.PE_SIG_LENGTH;
		byte[] headerbytes = loadBytes(offset, COFFFileHeader.HEADER_SIZE, raf);
		return new COFFFileHeader(headerbytes);
	}

	private OptionalHeader loadOptionalHeader(PESignature pesig,
			COFFFileHeader coff, RandomAccessFile raf) throws IOException {
		long offset = pesig.getPEOffset() + PESignature.PE_SIG_LENGTH
				+ COFFFileHeader.HEADER_SIZE;
		byte[] headerbytes = loadBytes(offset, coff.getSizeOfOptionalHeader(),
				raf);
		return new OptionalHeader(headerbytes);
	}

	private byte[] loadBytes(long offset, int length, RandomAccessFile raf)
			throws IOException {
		raf.seek(offset);
		byte[] bytes = new byte[length];
		raf.readFully(bytes);
		return bytes;
	}

	public static void main(String[] args) throws IOException {
		logger.entry();
//		File file = new File("src/main/resources/testfiles/strings.exe");
//		File file = new File("WinRar.exe");
		File file = new File("Holiday_Island.exe");
//		File file = new File("launch4jexe.exe");
		PEData data = PELoader.loadPE(file);
		SectionLoader loader = new SectionLoader(data);
		System.out.println(loader.loadResourceSection().getInfo());
	}

}