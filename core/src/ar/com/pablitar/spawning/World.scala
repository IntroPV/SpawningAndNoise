package ar.com.pablitar.spawning

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import org.apache.commons.math3.distribution.ExponentialDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import ar.com.pablitar.libgdx.commons.extensions.InputExtensions._
import ar.com.pablitar.libgdx.commons.extensions.VectorExtensions._
import ar.com.pablitar.libgdx.commons.traits.Positioned
import ar.com.pablitar.libgdx.commons.traits.AcceleratedSpeedBehaviour
import ar.com.pablitar.libgdx.commons.traits.DragBehaviour
import com.badlogic.gdx.Gdx
import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.math.MathUtils
import ar.com.pablitar.libgdx.commons.ColorUtils
import ar.com.pablitar.libgdx.commons.math.OpenSimplexNoise
import scala.util.Random

case class Tile(world: World, x: Int, y: Int) {
  val oddHue = 80
  val evenHue = 120
  def isOdd = (x + y) % 2 != 0
  def hue= if (isOdd) oddHue else evenHue
  val random = new Random(x+y)
  
  val smoothing = 10
  
  val z = world.terrainNoise.eval(x.toFloat / smoothing, y.toFloat / smoothing).toFloat 
  
  val color = ColorUtils.fromHSV(hue, -z * 35 + 35, z * 25 + 75)

  def tileSize = world.tileSize

  def width = tileSize
  def height = tileSize

  def worldPosition = new Vector2(x * tileSize, y * tileSize)
}

class Mob(p: Vector2) extends Positioned {
  this.position = p
  val radius = MathUtils.random(20f, 40f)
  val color = ColorUtils.fromHSV(MathUtils.random(270, 290), MathUtils.random(30, 50), MathUtils.random(70, 90))
}

class World {
  val tileSize = 40
  val extraTiles = Math.max((Spawning.VIEWPORT_WIDTH / tileSize) * 0.10, 2).toInt
  val horizontalTileCount = (Spawning.VIEWPORT_WIDTH / tileSize) + extraTiles
  val verticalTileCount = (Spawning.VIEWPORT_HEIGHT / tileSize) + extraTiles
  
  val seed = MathUtils.random(Long.MaxValue)
  val terrainNoise = new OpenSimplexNoise(seed)
  
  val mobs = ArrayBuffer.empty[Mob]

  val spawnTimeDistribution = new ExponentialDistribution(2)
  val spawnPositionDistribution = new NormalDistribution(0, Spawning.VIEWPORT_WIDTH / 8)

  object CameraSubject extends Positioned with AcceleratedSpeedBehaviour with DragBehaviour {
    val drag = 600f
    val accelerationMagnitude = 1500f
    override val topSpeedMagnitude = Some(1000f)
    def activeAcceleration: Option[Vector2] = Gdx.input.arrowsDirectionOption.map(_ * accelerationMagnitude)
  }

  var nextSpawnTime = spawnTimeDistribution.sample()

  def update(delta: Float) = {
    nextSpawnTime = nextSpawnTime - delta
    if (nextSpawnTime <= 0) spawn()
  }

  def generatePosition() = {
    val posX = spawnPositionDistribution.sample() + CameraSubject.position.x
    val posY = spawnPositionDistribution.sample() + CameraSubject.position.y

    new Vector2(posX.toFloat, posY.toFloat)
  }

  def spawn() = {
    val position = generatePosition()
    println(s"Spawning: $position")
    mobs += new Mob(position)
    nextSpawnTime = spawnTimeDistribution.sample() + 2
    println(s"Next spawn Time: $nextSpawnTime")
  }

}