package ar.com.pablitar.spawning

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.FitViewport
import ar.com.pablitar.libgdx.commons.rendering.Renderers
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import ar.com.pablitar.libgdx.commons.traits.AcceleratedSpeedBehaviour
import ar.com.pablitar.libgdx.commons.traits.DragBehaviour
import ar.com.pablitar.libgdx.commons.traits.Positioned
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

object Spawning {
  val VIEWPORT_WIDTH = 1280
  val VIEWPORT_HEIGHT = 720
}

class Spawning extends ApplicationAdapter {
  import Spawning._

  val world = new World()

  lazy val camera = {
    val cam = new OrthographicCamera()
    cam.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT)
    cam
  }

  lazy val viewport = {
    val vp = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera)
    vp.apply(true)
    vp
  }

  lazy val renderers = new Renderers
  lazy val worldRenderer = new WorldRenderer(renderers)

  override def render() {
    val delta = Gdx.graphics.getDeltaTime
    world.update(delta)
    moveCamera(delta)
    camera.update();

    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    renderers.begin()
    renderers.setProjectionMatrix(camera.combined)
    worldRenderer.render(world)
    renderers.end()
  }

  def moveCamera(delta: Float) = {
    world.CameraSubject.updateValues(delta)
    camera.position.set(world.CameraSubject.position, camera.position.z)
  }

}

class WorldRenderer(renderers: Renderers) {
  def render(world: World) = {
    renderFloor(world)
    renderMobs(world)
  }
  def visibleTiles(world: World) = {
    def tileSize = world.tileSize

    val horizontalOffset = (world.CameraSubject.position.x / tileSize).toInt - world.horizontalTileCount / 2
    val verticalOffset = (world.CameraSubject.position.y / tileSize).toInt - world.verticalTileCount / 2

    for (
      i <- horizontalOffset.to(horizontalOffset + world.horizontalTileCount);
      j <- verticalOffset.to(verticalOffset + world.verticalTileCount)
    ) yield {
      Tile(world, i, j)
    }
  }

  def renderFloor(world: World): Unit = {
    visibleTiles(world).foreach(renderTile(_))
  }

  def renderTile(aTile: Tile) = {
    renderers.withShapes(ShapeType.Filled) { sr =>
      val p = aTile.worldPosition
      sr.setColor(aTile.color)
      sr.rect(p.x, p.y, aTile.width, aTile.height)
    }
  }

  def renderMobs(world: World) = {
    world.mobs.foreach(renderMob(_))
  }

  def renderMob(aMob: Mob) = {
    renderers.withShapes(ShapeType.Filled) { sr =>
      sr.setColor(aMob.color)
      sr.circle(aMob.x, aMob.y, aMob.radius)
    }
  }
}