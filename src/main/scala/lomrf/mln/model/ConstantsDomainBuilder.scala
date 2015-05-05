/*
 * o                        o     o   o         o
 * |             o          |     |\ /|         | /
 * |    o-o o--o    o-o  oo |     | O |  oo o-o OO   o-o o   o
 * |    | | |  | | |    | | |     |   | | | |   | \  | |  \ /
 * O---oo-o o--O |  o-o o-o-o     o   o o-o-o   o  o o-o   o
 *             |
 *          o--o
 * o--o              o               o--o       o    o
 * |   |             |               |    o     |    |
 * O-Oo   oo o-o   o-O o-o o-O-o     O-o    o-o |  o-O o-o
 * |  \  | | |  | |  | | | | | |     |    | |-' | |  |  \
 * o   o o-o-o  o  o-o o-o o o o     o    | o-o o  o-o o-o
 *
 * Logical Markov Random Fields.
 *
 * Copyright (C) 2012  Anastasios Skarlatidis.
 *
 * self program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * self program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with self program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lomrf.mln.model

import lomrf.util.ConstantsSetBuilder

import scala.collection.mutable


class ConstantsDomainBuilder extends mutable.Builder[(String, String), ConstantsDomain]{ self =>

  private var constantBuilders = Map.empty[String, ConstantsSetBuilder]

  private var dirty = false

  def apply(key: String) = constantBuilders(key)

  def apply(): Map[String, ConstantsSetBuilder] = constantBuilders

  def update(input: Map[String, ConstantsSetBuilder]): self.type ={
    constantBuilders = input
    dirty = false
    self
  }

  def get(key: String) = constantBuilders.get(key)

  def += (key: String, value: String): self.type ={
    copyIfDirty()

    constantBuilders.get(key) match {
      case Some(builder) => builder += value
      case _ => constantBuilders += (key -> ConstantsSetBuilder(value))
    }

    self
  }

  override def += (entry: (String, String)): self.type = self += (entry._1, entry._2)

  def ++= (entry: (String, Iterable[String])): self.type ={
    copyIfDirty()

    val (key, values) = entry

    constantBuilders.get(key) match {
      case Some(builder) => builder ++= values
      case _ => constantBuilders += (key -> ConstantsSetBuilder(values))
    }

    self
  }

  def += (key: String): self.type ={
    copyIfDirty()

    constantBuilders.get(key) match {
      case None => constantBuilders += (key -> ConstantsSetBuilder())
      case _ => // do nothing
    }
    self
  }

  def ++= (keys: Iterable[String]): self.type ={
    copyIfDirty()

    for(key <- keys) {
      constantBuilders.get(key) match {
        case None => constantBuilders += (key -> ConstantsSetBuilder())
        case _ => // do nothing
      }
    }

    self
  }

  override def clear(): Unit = constantBuilders = Map.empty

  override def result(): ConstantsDomain = {
    dirty = true
    constantBuilders.mapValues(_.result())
  }


  private def copyIfDirty(): Unit ={
    if(self.dirty) {
      constantBuilders = constantBuilders.map{case (k, v) => k -> v.copy()}
      dirty = false
    }
  }

}

object ConstantsDomainBuilder{

  def apply(): ConstantsDomainBuilder = new ConstantsDomainBuilder()

  def apply(initial: Map[String, ConstantsSetBuilder]): ConstantsDomainBuilder ={
    val builder = new ConstantsDomainBuilder()
    builder() = initial
    builder
  }
}
