/*
 * Copyright 2019 PingCAP, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import org.apache.spark.SharedSparkContext._

trait SharedSparkContext extends BeforeAndAfterAll with BeforeAndAfterEach { self: Suite =>

  def sc: SparkContext = _sc

  protected def conf: SparkConf = new SparkConf(false)

  protected def initializeContext(): Unit = {
    if (null == _sc) {
      _sc = new SparkContext("local[4]", "test", conf)
    }
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    initializeContext()
  }

  override protected def afterAll(): Unit = {
    try {
      SharedSparkContext.stop(_sc)
    } finally {
      super.afterAll()
    }
  }
}

object SharedSparkContext {

  @transient private var _sc: SparkContext = _

  def getInstance(): SparkContext = _sc

  def stop(sc: SparkContext): Unit = {
    if (sc != null) {
      sc.stop()
    }
    // To avoid RPC rebinding to the same port, since it doesn't unbind immediately on shutdown
    System.clearProperty("spark.driver.port")
  }

}
