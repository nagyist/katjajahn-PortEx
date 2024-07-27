/** *****************************************************************************
 * Copyright 2024 Karsten Phillip Boris Hahn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * **************************************************************************** */

package com.github.struppigel.tools.rehints.scanning

import com.github.struppigel.tools.rehints.ReHintScannerUtils.{hasAnomaly, filterAnomalies}
import com.github.struppigel.tools.rehints.{ReHint, ReHintScanner, ReHintType, StandardReHint}

import scala.collection.mutable.ListBuffer
import com.github.struppigel.parser.IOUtil.NL
import com.github.struppigel.tools.anomalies.AnomalySubType

import scala.collection.JavaConverters._

trait ProcessInjectionScanning extends ReHintScanner {

  abstract override def scanReport(): String =
  "Applied FakeVMPScanning" + NL + super.scanReport

  abstract override def scan(): List[ReHint] = {
    val reList = ListBuffer[ReHint]()
    reList ++= checkThreadNameCalling()
    super.scan ::: reList.toList
  }

  private def checkThreadNameCalling(): List[ReHint] = {
    val importNames = List("GetThreadDescription", "SetThreadDescription")
    if(importNames.forall(hasAnomaly(anomalies, _, AnomalySubType.PROCESS_INJECTION_IMPORT))) {
      val filtered = filterAnomalies(anomalies, importNames, AnomalySubType.PROCESS_INJECTION_IMPORT)
      List(StandardReHint(filtered.asJava, ReHintType.THREAD_NAME_CALLING_INJECTION_HINT))
    } else Nil
  }

}
