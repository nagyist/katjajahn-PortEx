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
package com.github.katjahahn.sections;

import java.io.IOException;

import com.github.katjahahn.PEModule;

/**
 * Holds the bytes of a PESection.
 * 
 * @author Katja Hahn
 *
 */
public class PESection extends PEModule {

	private byte[] sectionbytes;
	
	protected PESection() {}

	public PESection(byte[] sectionbytes) {
		this.sectionbytes = sectionbytes.clone();
	}
	
	public byte[] getDump() {
		return sectionbytes.clone();
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public void read() throws IOException {
		
	}

}