package com.assignment

import zio.blocking.Blocking
import zio.stream.ZStream

package object domain {
  type MeasurementsStream = ZStream[Blocking, Throwable, Option[SensorMeasurement]]
}
